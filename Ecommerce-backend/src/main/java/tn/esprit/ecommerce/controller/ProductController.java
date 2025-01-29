package tn.esprit.ecommerce.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.ecommerce.request.ProductRequest;
import tn.esprit.ecommerce.response.ProductResponse;
import tn.esprit.ecommerce.service.ProductService;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.enums.Category;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping( "produit")
@RequiredArgsConstructor
public class ProductController {

private final ProductService productService;

    @PostMapping("/add-product")
    public ResponseEntity<Product> addProduct(@Valid @RequestBody ProductRequest request) {

        Product product = productService.addProduct(request);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/get-all-products")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
      return ResponseEntity.ok(productService.getAllProduits());
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
    @PostMapping(value = "/upload/{product-id}", consumes = "multipart/form-data")
    public ResponseEntity<?> uploadProductPicture(
            @PathVariable("product-id") String productId,

            @RequestPart("file") MultipartFile file

    ) {
        productService.uploadPhotoForProduct(file, productId);
        return ResponseEntity.accepted().build();
    }

}
