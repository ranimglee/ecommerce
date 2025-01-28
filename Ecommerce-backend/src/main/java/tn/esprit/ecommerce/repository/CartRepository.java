package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.User;

import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
}
