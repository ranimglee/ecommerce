package tn.esprit.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ecommerce.request.AuthenticationRequest;
import tn.esprit.ecommerce.response.AuthenticationResponse;
import tn.esprit.ecommerce.request.RegistrationRequest;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.UserRepository;
import tn.esprit.ecommerce.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping( "auth")
@RequiredArgsConstructor
public class UserController {
private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request)   {
        userService.register(request);
        return ResponseEntity.accepted().build();
    }
    @GetMapping("/confirm-account")
    public ResponseEntity<String> confirmAccount(@RequestParam("token") String confirmationToken) {
        // Call the authentication service method to confirm the account
        boolean isAccountConfirmed = userService.confirmAccount(confirmationToken);

        if (isAccountConfirmed) {
            return ResponseEntity.ok("Your account has been successfully confirmed.");
        } else {
            return ResponseEntity.badRequest().body("Invalid confirmation token.");
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(userService.authenticate(request));
    }
    // Méthode pour récupérer l'utilisateur connecté à partir du contexte de sécurité
    public Optional<User> getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByEmail(username);
    }
}
