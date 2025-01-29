package tn.esprit.ecommerce.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserProfileResponse {
    private String firstName;
    private String lastName;
    private String email;
}
