package tn.esprit.ecommerce.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.ecommerce.entity.Role;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
