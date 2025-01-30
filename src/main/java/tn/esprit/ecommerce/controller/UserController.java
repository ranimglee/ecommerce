package tn.esprit.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.request.AuthenticationRequest;
import tn.esprit.ecommerce.request.UserProfileRequest;
import tn.esprit.ecommerce.response.AuthenticationResponse;
import tn.esprit.ecommerce.request.RegistrationRequest;
import tn.esprit.ecommerce.repository.UserRepository;
import tn.esprit.ecommerce.service.UserService;

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



    @PutMapping("/update-profile")
    public ResponseEntity<User> updateProfile(@RequestBody UserProfileRequest request) {
        User updatedUser = userService.updateUserProfile(request);
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/my-profile")
    public ResponseEntity<User> getProfile() {
        // Retrieve the authenticated user
        User user = userService.getUserProfile();
        return ResponseEntity.ok(user);
    }

    // Delete user account
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(@RequestParam String email) {
        userService.deleteUserAccount(email);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all-clients")
    public ResponseEntity<Page<User>> listAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Create a Pageable object for pagination
        Pageable pageable = PageRequest.of(page, size);

        // Retrieve paginated clients from the service
        Page<User> users = userService.listAllClients(pageable);

        // Return paginated list of users with CLIENT role
        return ResponseEntity.ok(users);
    }

    // Step 1: Request OTP for password reset
    @PostMapping("/request-otp")
    public ResponseEntity<String> requestOTP(@RequestParam String email) {
        userService.sendPasswordResetOTP(email);
        return ResponseEntity.ok("OTP sent to your email.");
    }

    // Step 2: Validate OTP
    @PostMapping("/validate-otp")
    public ResponseEntity<String> validateOTP(
            @RequestParam String email,
            @RequestParam String otp) {
        boolean isValid = userService.validateOTP(email, otp);
        if (isValid) {
            return ResponseEntity.ok("OTP validated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Invalid OTP.");
        }
    }

    // Step 3: Update password
    @PostMapping("/update-password")
    public ResponseEntity<String> updatePassword(
            @RequestParam String email,
            @RequestParam String newPassword) {
        userService.updatePassword(email, newPassword);
        return ResponseEntity.ok("Password updated successfully.");
    }

}
