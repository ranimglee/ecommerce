package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.repository.ProductRepository;

@RestController
@RequestMapping( "guest")
@RequiredArgsConstructor
public class GuestController {
private final ProductRepository productRepository;

    @GetMapping("/get-all-products")
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        System.out.println("Returned Page: " + productPage);
        return productPage;
    }
}
