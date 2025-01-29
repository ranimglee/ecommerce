package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.ecommerce.repository.ProductRepository;
import tn.esprit.ecommerce.service.ProductService;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.enums.Category;

import java.util.Optional;

@RestController
@RequestMapping( "product")
@RequiredArgsConstructor
public class ProductController {

private final ProductService productService;
    private final ProductRepository productRepository;

    @PostMapping("/add-product")
    public ResponseEntity<Product> addProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int quantity,
            @RequestParam double price,
            @RequestParam MultipartFile imageFile,
            @RequestParam Category category) {

        Product product = productService.addProduct(name, description, quantity, price, imageFile, category);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/get-all-products")
    public Page<Product> getAllProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        System.out.println("Returned Page: " + productPage);
        return productPage;
    }



    @GetMapping("get-product-by-id/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> productOpt = productService.getProductById(id);
        return productOpt.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("delete-product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        boolean isDeleted = productService.deleteProduct(id);
        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("update-product/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) Integer quantity,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) MultipartFile imageFile,
            @RequestParam(required = false) Category category) {


        Product updatedProduct = productService.updateProduct(
                id,
                name,
                description,
                quantity,
                price,
                imageFile,
                category
        );

        return ResponseEntity.ok(updatedProduct);
    }

}
