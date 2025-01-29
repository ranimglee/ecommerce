package tn.esprit.ecommerce.response;

import lombok.*;
import tn.esprit.ecommerce.enums.Category;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class ProductResponse {
    private String id;

    private String name;
    private String description;
    private double price;
    private Category category;
    private int quantity;
    private byte[] image ;
}
