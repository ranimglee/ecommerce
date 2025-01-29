package tn.esprit.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.ecommerce.util.FileNamingUtil;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.enums.Category;
import tn.esprit.ecommerce.repository.ProductRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final FileNamingUtil fileNamingUtil;

    @Value("${uploadProductImages}")
    private String uploadProductImages;


    public Product addProduct(String name, String description, int quantity,
                              double price, MultipartFile imageFile,
                              Category category) {
        try {
            // Save the image to the server
            // TODO: Add validation for `name`, `description`, `price`, and `quantity` (e.g., non-null, positive values).
            // TODO: Add a request product DTO
            String fileName = fileNamingUtil.nameFile(imageFile);
            Path destinationPath = Paths.get(uploadProductImages, fileName);
            Files.copy(imageFile.getInputStream(), destinationPath);


            // Build the new product using the builder pattern
            Product newProduct = Product.builder()
                    .name(name)
                    .description(description)
                    .quantity(quantity)
                    .price(price)
                    .category(category)
                    .image(fileName)
                    .build();

            // Save the product to the database
            return productRepository.save(newProduct);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
    // Lire tous les produits
    public List<Product> getAllProduits() {
        return productRepository.findAll();
    }

    public boolean deleteProduit(String id) {
        Optional<Product> produitOpt = productRepository.findById(id);
        if (produitOpt.isPresent()) {
            productRepository.delete(produitOpt.get());
            return true;
        }
        return false;
    }

    public Optional<Product> getProduitById(String id) {
        return productRepository.findById(id);
    }


    public Product updateProduct(String id, String name, String description, Integer quantity,
                                 Double price, MultipartFile imageFile,
                                 Category category) {
        // Retrieve the existing product by ID
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product with ID " + id + " not found"));

        // Update only the fields that are provided (non-null)
        if (name != null && !name.isEmpty()) {
            existingProduct.setName(name);
        }
        if (description != null && !description.isEmpty()) {
            existingProduct.setDescription(description);
        }
        if (quantity != null) {
            existingProduct.setQuantity(quantity);
        }
        if (price != null) {
            existingProduct.setPrice(price);
        }
        if (category != null) {
            existingProduct.setCategory(category);
        }

        // Handle image upload if a new image is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // Generate a new file name and save the image
                String fileName = fileNamingUtil.nameFile(imageFile);
                Path destinationPath = Paths.get(uploadProductImages, fileName);
                Files.copy(imageFile.getInputStream(), destinationPath);

                // Update the product's image field
                existingProduct.setImage(fileName);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image", e);
            }
        }

        // Save the updated product to the database
        return productRepository.save(existingProduct);
    }

}
