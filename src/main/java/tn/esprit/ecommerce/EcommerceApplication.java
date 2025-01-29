package tn.esprit.ecommerce;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import tn.esprit.ecommerce.entity.Role;
import tn.esprit.ecommerce.repository.RoleRepository;

@SpringBootApplication
@EnableAsync


public class EcommerceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EcommerceApplication.class, args);
    }
    @Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByName("CLIENT").isEmpty()) {
				roleRepository.save(Role.builder().name("CLIENT").build());
			}
		};
	}
}

