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
                if (product == null) {
                        return 0.0;  // or handle the null case appropriately
                }
                return product.getPrice() * quantity;  // assuming you have quantity in CartItem
        }


}