package tn.esprit.ecommerce.request;

import lombok.*;
import tn.esprit.ecommerce.enums.Category;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder

public class ProductRequest {
    private String id;

    private String name;
    private String description;
    private double price;
    private Category category;
    private int quantity;

}
