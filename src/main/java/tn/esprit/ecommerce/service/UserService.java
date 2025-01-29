package tn.esprit.ecommerce.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.JWT.JwtService;
import tn.esprit.ecommerce.exception.EmailExistsExecption;
import tn.esprit.ecommerce.exception.EmailSendingException;
import tn.esprit.ecommerce.repository.RoleRepository;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.UserRepository;
import tn.esprit.ecommerce.request.AuthenticationRequest;
import tn.esprit.ecommerce.request.RegistrationRequest;
import tn.esprit.ecommerce.response.AuthenticationResponse;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String emailUsername;

    @Value("${spring.mail.app-password}")
    private String emailAppPassword;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


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
        sendConfirmationEmail(user.getEmail(), confirmationToken);
    }


    private String generateConfirmationToken() {
        return UUID.randomUUID().toString();
    }



    private void sendConfirmationEmail(String email, String confirmationToken) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            // Configure the helper with the sender email properties
            helper.setFrom(emailUsername);
            helper.setTo(email);
            helper.setSubject("Confirm your account");
            helper.setText("To confirm your account, please click the link below:\n"
                    + "http://localhost:8080/api/v1/auth/confirm-account?token=" + confirmationToken);
        } catch (MessagingException e) {
            // Throw a custom exception with the error message and the cause
            throw new EmailSendingException("Failed to send confirmation email to " + email, e);
        }

        if (emailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) emailSender;
            mailSenderImpl.setUsername(emailUsername);
            mailSenderImpl.setPassword(emailAppPassword);
        }

        try {
            emailSender.send(message);
        } catch (Exception e) {
            // Handle failure in sending email (for example network or server issues)
            throw new EmailSendingException("Error occurred while sending the confirmation email to " + email, e);
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
        Optional<User> userOptional = userRepository.findByConfirmationToken(confirmationToken);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

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
    public void resetPassword(String username) {
        Optional<User> userOptional = userRepository.findByEmail(username);

        if (userOptional.isEmpty()) {
            throw new EmailExistsExecption("Email not found: " + username);
        }

        User user = userOptional.get();
        String resetToken = UUID.randomUUID().toString();
        user.setPasswordResetToken(resetToken);
        // Set token expiry to 1 hour from now
        LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
        user.setTokenExpiry(expiryDate);
        userRepository.save(user);

        sendPasswordResetEmail(user.getEmail(), resetToken);
    }


    private void sendPasswordResetEmail(String email, String resetToken) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            helper.setFrom(emailUsername);
            helper.setTo(email);
            helper.setSubject("Password Reset Request");
            helper.setText("To reset your password, click the link below:\n"
                    + "http://localhost:8080/api/v1/auth/reset-password?token=" + resetToken);
        } catch (MessagingException e) {
            throw new EmailSendingException("Failed to send password reset email", e);
        }

        emailSender.send(message);
    }

    public boolean validateResetToken(String token) {
        User user = (User) userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        // Check if the token expiry date is null
        if (user.getTokenExpiry() == null) {
            throw new RuntimeException("Token expiry is not set");
        }

        // Convert Instant.now() to LocalDateTime to match user.getTokenExpiry() type
        LocalDateTime now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Check if the token has expired
        if (user.getTokenExpiry().isBefore(now)) {
            throw new RuntimeException("Token expired");
        }

        return true;
    }


    public void updatePassword(String token, String newPassword) {
        User user = (User) userRepository.findByPasswordResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        LocalDateTime now = Instant.now().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Check if the token has expired
        if (user.getTokenExpiry().isBefore(now)) {
            throw new RuntimeException("Token expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword)); // Hachage du mot de passe
        user.setPasswordResetToken(null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }


}
