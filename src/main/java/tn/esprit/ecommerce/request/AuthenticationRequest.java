package tn.esprit.ecommerce.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AuthenticationRequest {
    @Valid
    @Email(message = "inavalid mail format")
    @NotBlank(message = "email is required and cannot be blank.")
    private String email;
    @NotBlank(message = "Password is mandatory")
    @Size(min = 8, message = "Password should be 8 characters long minimum")
    private String password;
}