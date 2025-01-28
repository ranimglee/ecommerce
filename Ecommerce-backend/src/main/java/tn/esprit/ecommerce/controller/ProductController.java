package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.ecommerce.service.ProductService;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.enums.Category;

import java.util.List;
import java.util.Optional;

@RestController
// TODO: use only one language when writing your code
@RequestMapping( "produit")
@RequiredArgsConstructor
public class ProductController {

private final ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<Product> addProduct(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam int quantity,
            @RequestParam double price,
            @RequestParam MultipartFile imageFile,
            @RequestParam Category category) {

        // TODO: Validate inputs (e.g., check if name, description, or price are valid).
        // TODO: Use DTO for adding a product (e.g., addProductRequest).
        Product product = productService.addProduct(name, description, quantity, price, imageFile, category);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProduits();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @GetMapping("get-product-by-id/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> produitOpt = productService.getProduitById(id);
        return produitOpt.map(produit -> new ResponseEntity<>(produit, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @DeleteMapping("delete-product/{id}")
    public ResponseEntity<Void> deleteProduit(@PathVariable String id) {
        boolean isDeleted = productService.deleteProduit(id);
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
