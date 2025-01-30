package tn.esprit.ecommerce.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "order_lines")
public class OrderLine {
    @Id
    private String id;

    private int quantity;

    private Double sousTotal;

    @DBRef
    private Product product;

    @DBRef
    private Order order;



}
