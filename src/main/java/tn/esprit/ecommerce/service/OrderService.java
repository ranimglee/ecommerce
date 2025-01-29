package tn.esprit.ecommerce.service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.entity.*;
import tn.esprit.ecommerce.enums.OrderStatus;
import tn.esprit.ecommerce.repository.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {


    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;
    private final PaymentService paymentService;

    public Order passCommand(User user, Cart cart, String paymentMethodId) throws StripeException {
        // Validate that all CartItems have IDs
        for (CartItem cartItem : cart.getCartItems()) {
            if (cartItem.getId() == null) {
                throw new IllegalArgumentException("CartItem must have an ID");
            }
        }

        // Create the OrderLines from CartItems with calculated subtotal
        List<OrderLine> orderLines = cart.getCartItems().stream()
                .map(cartItem -> {
                    double sousTotal = cartItem.getProduct().getPrice() * cartItem.getQuantity();  // Calculate subtotal
                    OrderLine orderLine = OrderLine.builder()
                            .product(cartItem.getProduct())
                            .quantity(cartItem.getQuantity())
                            .sousTotal(sousTotal)  // Set calculated subtotal
                            .build();
                    return orderLine;
                })
                .collect(Collectors.toList());

        // Save all OrderLines in bulk (instead of saving each one individually)
        orderLineRepository.saveAll(orderLines);

        // Create the Order and set status to PENDING
        Order order = Order.builder()
                .user(user)
                .date(LocalDate.now())
                .orderLines(orderLines)
                .status(OrderStatus.PENDING)
                .build();

        // Calculate the total based on OrderLines
        order.calculateTotal();

        // Now, create a PaymentIntent on Stripe
        PaymentIntent paymentIntent = paymentService.createPaymentIntent(order.getTotal(), paymentMethodId);

        // If the payment intent is confirmed, proceed to save the order
        if (paymentIntent.getStatus().equals("succeeded")) {
            order.setPaymentIntentId(paymentIntent.getId());
            order.setStatus(OrderStatus.PAID);
            return orderRepository.save(order);
        } else {
            throw new RuntimeException("Payment failed or was not confirmed");
        }
    }




  /*  public boolean cancelOrder(User user, String orderId) {

        // Fetch the order based on the provided order ID
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null || !order.getUser().equals(user)) {
            throw new RuntimeException("Order not found or user does not have permission to cancel it.");
        }

        // Check if the order can be canceled
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.PAID) {
            throw new RuntimeException("This order cannot be canceled.");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return true;
    }

*/

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
