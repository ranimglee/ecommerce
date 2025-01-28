package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.Order;
import tn.esprit.ecommerce.entity.User;

import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUser(User user);
}
