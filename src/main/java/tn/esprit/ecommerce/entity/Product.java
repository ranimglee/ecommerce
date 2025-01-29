package tn.esprit.ecommerce.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import tn.esprit.ecommerce.enums.Category;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "products")
@Builder
public class Product {

    @Id
    private String id;

    private String name;
    private String description;
    private double price;
    private Category category;
    private int quantity;
    private String image ;



}
