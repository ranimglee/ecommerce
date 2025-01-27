package tn.esprit.ecommerce.User;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String userEmail);

    Optional<User> findByConfirmationToken(String confirmationToken);
}
