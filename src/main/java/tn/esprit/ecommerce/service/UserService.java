package tn.esprit.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import tn.esprit.ecommerce.security.JWT.JwtService;
import tn.esprit.ecommerce.exception.EmailExistsExecption;
import tn.esprit.ecommerce.exception.EmailSendingException;
import tn.esprit.ecommerce.repository.RoleRepository;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.UserRepository;
import tn.esprit.ecommerce.request.AuthenticationRequest;
import tn.esprit.ecommerce.request.RegistrationRequest;
import tn.esprit.ecommerce.request.UserProfileRequest;
import tn.esprit.ecommerce.response.AuthenticationResponse;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.app-password}")
    private String emailAppPassword;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final TemplateEngine templateEngine;


    public void register(@Valid RegistrationRequest request) {
        var userRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("ROLE CLIENT was not initiated"));

        // Check if the user already exists using the email
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        if (existingUser.isPresent()) {
            throw new EmailExistsExecption("Email already exists");
        }
        // Generate confirmation token
        String confirmationToken = generateConfirmationToken();
        var user =User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .confirmationToken(confirmationToken)
                .enabled(false)
                .roles(List.of(userRole)).build();

        userRepository.save(user);
        // Send confirmation email
        try {
            sendConfirmationEmail(user.getEmail(), confirmationToken);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send confirmation email to " + user.getEmail(), e);
        }
    }


    private String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }



    private void sendConfirmationEmail(String email, String confirmationToken) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart email (for HTML support)
        try {
            // Prepare the Thymeleaf context
            Context context = new Context();
            context.setVariable("confirmationToken", confirmationToken);
          //  context.setVariable("user", user);

            // Generate HTML content using Thymeleaf template
            String htmlContent = templateEngine.process("confirmation-email", context); // Name of the template file

            // Configure the email properties
            helper.setFrom(emailUsername);

            helper.setTo(email);
            helper.setSubject("Confirm your account");
            helper.setText(htmlContent, true);
            if (emailSender instanceof JavaMailSenderImpl mailSenderImpl) {
                mailSenderImpl.setUsername(emailUsername);
                mailSenderImpl.setPassword(emailAppPassword);
            }

            try {
                emailSender.send(message);
            } catch (Exception e) {
                // Handle failure in sending email (for example network or server issues)
                throw new EmailSendingException("Error occurred while sending the confirmation email to " + email, e);
            }
        } catch (Exception e) {
            throw new EmailSendingException("Failed to send confirmation email to " + email, e);
        }


    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var claims=new HashMap<String, Object>();
        var user=((User) auth.getPrincipal());
        claims.put("fullName",user.fullName());
        var jwt=jwtService.generateToken(claims,user);
        return AuthenticationResponse.builder()
                .accessToken(jwt)
                .build();
    }

    public User findByUsername(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean confirmAccount(String confirmationToken) {
        System.out.println("Received token: " + confirmationToken); // Log the received token
        Optional<User> userOptional = userRepository.findByConfirmationToken(confirmationToken);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            System.out.println("Stored token: " + user.getConfirmationToken()); // Log the stored token

            if (user.isEnabled()) {
                return false; // Account already confirmed
            }

            user.setEnabled(true);
            user.setConfirmationToken(null);  // Optionally nullify the token after use
            userRepository.save(user);

            return true; // Account confirmed successfully
        } else {
            return false; // Invalid or expired confirmation token
        }
    }


    public User updateUserProfile(UserProfileRequest request) {
        // Retrieve the currently authenticated user’s email from SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        // Find the user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the user's profile
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail()); // Update email if needed (make sure it's not already in use)

        return userRepository.save(user);
    }


    public User getUserProfile() {
        // Retrieve the currently authenticated user’s email from SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email = userDetails.getUsername();

        // Find the user by email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public void deleteUserAccount(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        userRepository.delete(user);
    }

    public Page<User> listAllClients(Pageable pageable) {
        // Get the "CLIENT" role from the role repository
        var clientRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new RuntimeException("Role CLIENT not found"));

        // Find all users that have the "CLIENT" role, with pagination
        return userRepository.findByRolesContaining(clientRole, pageable);
    }





    // Step 1: Generate and send OTP
    public void sendPasswordResetOTP(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isEmpty()) {
            throw new EmailExistsExecption("Email not found: " + email);
        }

        User user = userOptional.get();
        String otp = generateOTP();
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES);

        user.setOtp(otp);
        user.setOtpExpiry(expiryDate);
        userRepository.save(user);

        sendOTPEmail(user.getEmail(), otp);
    }

    // Step 2: Generate a random OTP
    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return otp.toString();
    }

    // Step 3: Send OTP via email
    private void sendOTPEmail(String email, String otp) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(emailUsername);
            helper.setTo(email);
            helper.setSubject("Password Reset OTP");
            helper.setText("Your OTP for password reset is: " + otp + "\nThis OTP is valid for " + OTP_EXPIRY_MINUTES + " minutes.");
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send OTP email", e);
        }

        emailSender.send(message);
    }

    // Step 4: Validate OTP
    public boolean validateOTP(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found: " + email));

        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new RuntimeException("OTP not generated or expired");
        }

        LocalDateTime now = LocalDateTime.now();

        if (user.getOtpExpiry().isBefore(now)) {
            throw new RuntimeException("OTP expired");
        }

        if (!user.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        // Clear OTP after successful validation
        user.setOtp(null);
        user.setOtpExpiry(null);
        userRepository.save(user);

        return true; // OTP is valid
    }

    // Step 5: Update password
    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found: " + email));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
