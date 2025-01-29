package tn.esprit.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import tn.esprit.ecommerce.entity.Role;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.RoleRepository;
import tn.esprit.ecommerce.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableAsync
public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
    @Bean
	public CommandLineRunner runner(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (roleRepository.findByName("CLIENT").isEmpty()) {
				roleRepository.save(Role.builder().name("CLIENT").build());
			}
			if (roleRepository.findByName("ADMIN").isEmpty()) {
				roleRepository.save(Role.builder().name("ADMIN").build());
			}
			if (userRepository.findByEmail("admin@example.com").isEmpty()) {
				List<Role> adminRoles = new ArrayList<>();
				adminRoles.add(roleRepository.findByName("ADMIN").orElseThrow(() -> new RuntimeException("Role ADMIN not found")));

				User adminUser = User.builder()
						.firstName("Admin")
						.lastName("User")
						.email("admin@example.com")
						.password(passwordEncoder.encode("admin1234"))  // Encode the password
						.roles(adminRoles)
						.enabled(true)

						.build();

				userRepository.save(adminUser);

		};			};
	}
}

