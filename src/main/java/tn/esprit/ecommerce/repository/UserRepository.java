package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String userEmail);

    Optional<User> findByConfirmationToken(String confirmationToken);


    Optional<Object> findByPasswordResetToken(String token);
}
