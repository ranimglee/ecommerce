package tn.esprit.ecommerce.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationRequest {
    @Valid
    @NotBlank(message = "first name  is required and cannot be blank.")
    @Size(min=3,max = 25,message = "first name length min is 3 and max is 25")
    private String firstName;
    @NotBlank(message = "last name is required and cannot be blank.")
    @Size(min=3,max = 25,message = "last name length min is 3 and max is 25")
    private String lastName;
    @Size(min = 8,max = 22,message = "password should be min 8 caracters and 22 caracters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "Password must contain at least one lowercase letter, one uppercase letter, and one number.")
    private String password;
    @Email(message = "inavalid mail format")
    @NotBlank(message = "email is required and cannot be blank.")
    private String email;


}
