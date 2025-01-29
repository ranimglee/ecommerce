package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
}
