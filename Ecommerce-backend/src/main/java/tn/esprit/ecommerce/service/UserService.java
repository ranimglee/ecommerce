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
import tn.esprit.ecommerce.repository.RoleRepository;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.UserRepository;
import tn.esprit.ecommerce.request.AuthenticationRequest;
import tn.esprit.ecommerce.request.RegistrationRequest;
import tn.esprit.ecommerce.response.AuthenticationResponse;

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
            e.printStackTrace();
        }
        if (emailSender instanceof JavaMailSenderImpl) {
            JavaMailSenderImpl mailSenderImpl = (JavaMailSenderImpl) emailSender;
            mailSenderImpl.setUsername(emailUsername);
            mailSenderImpl.setPassword(emailAppPassword);
        }
        emailSender.send(message);
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
        // Find the user by the confirmation token
        Optional<User> userOptional = userRepository.findByConfirmationToken(confirmationToken);

        if (userOptional.isPresent()) {
            // Update user's account to confirmed
            User user = userOptional.get();
            user.setEnabled(true);
            userRepository.save(user);

            // Optional: You can also remove the confirmation token from the user entity

            return true; // Account confirmed successfully
        } else {
            return false; // Invalid confirmation token
        }
    }
}
