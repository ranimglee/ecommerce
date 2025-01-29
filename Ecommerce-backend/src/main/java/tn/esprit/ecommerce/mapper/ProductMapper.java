package tn.esprit.ecommerce.mapper;

import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.file.FileUtils;
import tn.esprit.ecommerce.request.ProductRequest;
import tn.esprit.ecommerce.response.ProductResponse;

@Service

public class ProductMapper {
    public Product toProduct(ProductRequest request) {
        return Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .category(request.getCategory())

                .build();

    }

    public ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .quantity(product.getQuantity())
                .price(product.getPrice())
                .category(product.getCategory())
                .image(FileUtils.readFileFromLocation(product.getImage()))
                .build();
    }
}
