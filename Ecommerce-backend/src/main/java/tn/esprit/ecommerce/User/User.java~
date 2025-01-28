package tn.esprit.ecommerce.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import tn.esprit.ecommerce.Role.Role;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
@Builder
public class User implements UserDetails , Principal {


    @Id
    private String id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String ConfirmationToken;
    private boolean enabled;

    @DBRef
    private List<Role> roles;


    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getName() {
        return email;
    }
    @Override
    public String getPassword() {
        return password;
    }

    public String fullName() {
        return firstName+" "+lastName;
    }

}
