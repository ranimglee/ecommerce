package tn.esprit.ecommerce.request;

import lombok.*;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.User;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OrderRequest {

    private User user;
    private Cart cart;
    private String paymentMethodId;
}