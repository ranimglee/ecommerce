package tn.esprit.ecommerce.User;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.JWT.JwtService;
import tn.esprit.ecommerce.Role.RoleRepository;

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
    public JavaMailSender emailSender;
    @Value("${spring.mail.username}") // Assuming you have the username configured in your application.properties file
    private String emailUsername;

    @Value("${spring.mail.app-password}") // Assuming you have configured the app password in your application.properties file
    private String emailAppPassword;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public void register(@Valid RegistrationRequest request) {
        var userRole = roleRepository.findByName("CLIENT")
                .orElseThrow(() -> new IllegalStateException("ROLE USER was not initiated"));


        // Generate confirmation token
        String confirmationToken = generateConfirmationToken();
        var user =User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .ConfirmationToken(confirmationToken)
                .enabled(false)
                .roles(List.of(userRole)).build();

        userRepository.save(user);
        // Send confirmation email
       // sendConfirmationEmail(user.getEmail(), confirmationToken);
    }
    private String generateConfirmationToken() {
        // Generate a random confirmation token logic here
        // You can use UUID.randomUUID() or any other method to generate a unique token
        return UUID.randomUUID().toString();
    }



 /*   private void sendConfirmationEmail(String email, String confirmationToken) {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        try {
            // Configure the helper with the sender email properties
            helper.setFrom(emailUsername); // Set the sender email address
            helper.setTo(email);
            helper.setSubject("Confirm your account");
            helper.setText("To confirm your account, please click the link below:\n"
                    + "http://localhost:8081/api/v1/auth/confirm-account?token=" + confirmationToken);
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

*/
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
                .token(jwt)
                .build();
    }

}
