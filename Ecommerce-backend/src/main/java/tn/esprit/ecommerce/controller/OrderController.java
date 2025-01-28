package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ecommerce.repository.OrderRepository;
import tn.esprit.ecommerce.service.OrderService;
import tn.esprit.ecommerce.entity.Order;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.User;

import java.util.List;
@RestController
@RequestMapping( "order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final CartController cartController;

    // TODO: Ensure that the @Secured annotations are correctly enabled in the security configuration.
    //@Secured("ROLE_ADMIN")
    @GetMapping("/admin/get-all-orders")
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // This endpoint will be for users if needed
   // @Secured("ROLE_USER")
    @GetMapping("/user/orders")
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    @PutMapping("/pass-order")
    public ResponseEntity<Order> passOrder(@RequestBody Cart cart) {
        User user = cartController.getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // TODO: Validate the `cart` object to ensure it contains valid data (e.g., non-empty product list).
        Order commande = orderService.passCommand(user, cart);
        return new ResponseEntity<>(commande, HttpStatus.CREATED);
    }

    @DeleteMapping("/cancel-order")
    public ResponseEntity<String> cancelOrder(@RequestParam String orderId) {
        User user = cartController.getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        // TODO: Validate the `orderId` parameter to ensure it is not null or empty.
        boolean isCanceled = orderService.cancelOrder(user, orderId);
        if (isCanceled) {
            return new ResponseEntity<>("Order canceled successfully", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to cancel the order", HttpStatus.BAD_REQUEST);
        }
    }

}
