package tn.esprit.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.ecommerce.file.FileStorageService;
import tn.esprit.ecommerce.mapper.ProductMapper;
import tn.esprit.ecommerce.request.ProductRequest;

import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.enums.Category;
import tn.esprit.ecommerce.repository.ProductRepository;
import tn.esprit.ecommerce.response.ProductResponse;


import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;
    private final ProductMapper productMapper;


    public Product addProduct(ProductRequest request) {

            Product product=productMapper.toProduct(request);
            return productRepository.save(product);


    }
    public void uploadPhotoForProduct(MultipartFile file, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("No product found with ID:: " + productId));

        var productPicture = fileStorageService.saveFile(file);
        product.setImage(productPicture);
        productRepository.save(product);
    }
    // Lire tous les produits
    public List<ProductResponse> getAllProduits() {
        return productRepository.findAll().stream().map(productMapper::toProductResponse).toList();
    }


    public boolean deleteProduct(String id) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isPresent()) {
            productRepository.delete(productOpt.get());
            return true;
        }
        return false;
    }

    public Optional<Product> getProductById(String id) {
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

//        // Handle image upload if a new image is provided
//        if (imageFile != null && !imageFile.isEmpty()) {
//            try {
//                // Generate a new file name and save the image
//                String fileName = fileNamingUtil.nameFile(imageFile);
//                Path destinationPath = Paths.get(uploadProductImages, fileName);
//                Files.copy(imageFile.getInputStream(), destinationPath);
//
//                // Update the product's image field
//                existingProduct.setImage(fileName);
//            } catch (IOException e) {
//                throw new RuntimeException("Failed to upload image", e);
//            }
//        }

        // Save the updated product to the database
        return productRepository.save(existingProduct);
    }

}
