package tn.esprit.ecommerce.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "carts")
public class Cart {
    @Id
    private String id;

    @DBRef
    private User user;

    @DBRef
    private List<CartItem> cartItems = new ArrayList<>();  // Initialize as an empty list

    /**
     * Calculate the total price of the cart.
     *
     * @return The total price of all items in the cart.
     */
    public double getTotal() {
        return cartItems.stream()
                .mapToDouble(CartItem::getSousTotal)
                .sum();
        // TODO: Handle cases where cartItems or getSousTotal might throw exceptions (e.g., null values).
    }
}
