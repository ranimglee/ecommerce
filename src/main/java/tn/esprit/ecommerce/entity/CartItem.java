package tn.esprit.ecommerce.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.logging.Logger;

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

        private static final Logger logger = Logger.getLogger(CartItem.class.getName());


        /**
         * Calculates the subtotal price for this cart item.
         *
         * @return The subtotal price based on the product price and quantity.
         */
        public double getSousTotal() {
                if (product == null) {
                        logger.warning("Product is null, returning 0.0");
                        return 0.0;
                }

                double price = product.getPrice();
                return price * quantity;
        }
}