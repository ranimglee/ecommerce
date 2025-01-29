package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.OrderLine;

public interface OrderLineRepository extends MongoRepository<OrderLine, String> {
}
