package tn.esprit.ecommerce.controller;

import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ecommerce.repository.OrderRepository;
import tn.esprit.ecommerce.request.OrderRequest;
import tn.esprit.ecommerce.service.OrderService;
import tn.esprit.ecommerce.entity.Order;
import tn.esprit.ecommerce.entity.User;

import java.util.List;

@RestController
@RequestMapping( "order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("/get-all-orders")
    public Page<Order> getAllOrders(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }


    @GetMapping("/user/orders")
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUser(user);
    }

    @PostMapping("/place-order")
    public ResponseEntity<Object> createOrder(@RequestBody OrderRequest orderRequest, @AuthenticationPrincipal User loggedInUser) {
        try {
            if (loggedInUser == null) {
                throw new RuntimeException("User not found");
            }

            Order order = orderService.passCommand(loggedInUser, orderRequest.getCart(), orderRequest.getPaymentMethodId());
            return new ResponseEntity<>(order, HttpStatus.CREATED);
        } catch (StripeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Payment failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Invalid input: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }







   /* @PostMapping("/create-payment-intent")
    public ResponseEntity<String> createPaymentIntent(@RequestBody PaymentInfo paymentInfo) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(paymentInfo);
            return ResponseEntity.ok("Payment Intent Created: " + paymentIntent.getId());
        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Payment failed: " + e.getMessage());
        }
    }*/
}