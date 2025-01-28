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
@Document(collection = "cart_items")
public class CartItem {
        @Id
        private String id;

        @DBRef
        private Product product;

        private int quantity;

        public double getSousTotal() {
            return product.getPrice() * quantity;
        }

}