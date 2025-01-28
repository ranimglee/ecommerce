package tn.esprit.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.entity.OrderLine;
import tn.esprit.ecommerce.enums.OrderStatus;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.CartItem;
import tn.esprit.ecommerce.entity.Order;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.repository.CartRepository;
import tn.esprit.ecommerce.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OrderService {

    private final CartRepository cartRepository;

    private final OrderRepository orderRepository;

    public Order passCommand(User user, Cart cart) {

        // Ensure the cart is valid before proceeding
        if (cart == null || cart.getCartItems().isEmpty()) {
            throw new RuntimeException("Cart is empty. Cannot place an order.");
        }
        List<OrderLine> orderLines = cart.getCartItems().stream()
                .map(this::convertToOrderLine)
                .collect(Collectors.toList());

        // Create a new Order from the Cart
        Order order = new Order();
        order.setUser(user);
        order.setOrderLines(orderLines);
        order.setStatus(OrderStatus.PENDING);  // Or any initial status for a new order

        // Save the order
        orderRepository.save(order);

        // Optionally, you can clear the cart after placing the order
        cartRepository.delete(cart); // Clear the cart

        return order;
    }

    public boolean cancelOrder(User user, String orderId) {

        // Fetch the order based on the provided order ID
        Order order = orderRepository.findById(orderId).orElse(null);

        if (order == null || !order.getUser().equals(user)) {
            throw new RuntimeException("Order not found or user does not have permission to cancel it.");
        }

        // Check if the order can be canceled
        if (order.getStatus() == OrderStatus.CANCELED || order.getStatus() == OrderStatus.COMPLETED) {
            throw new RuntimeException("This order cannot be canceled.");
        }

        order.setStatus(OrderStatus.CANCELED);
        orderRepository.save(order);

        return true;
    }

    private OrderLine convertToOrderLine(CartItem cartItem) {
        OrderLine ligneorder = new OrderLine();
        ligneorder.setProduct(cartItem.getProduct());
        ligneorder.setQuantity(cartItem.getQuantity());
        ligneorder.setSousTotal(cartItem.getSousTotal());
        return ligneorder;
    }
}
