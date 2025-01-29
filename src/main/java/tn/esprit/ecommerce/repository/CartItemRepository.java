package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.CartItem;

public interface CartItemRepository extends MongoRepository<CartItem, String> {
}
