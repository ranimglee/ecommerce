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


    /**
     * Calculates the subtotal price for this cart item.
     *
     * @return The subtotal price based on the product price and quantity.
     */
    public double getSousTotal() {
        // TODO: Handle cases where the product or product price might be null.
        return product.getPrice() * quantity;
    }

}