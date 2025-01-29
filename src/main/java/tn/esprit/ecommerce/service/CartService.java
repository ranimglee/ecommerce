package tn.esprit.ecommerce.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.CartItem;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.repository.CartItemRepository;
import tn.esprit.ecommerce.repository.CartRepository;
import tn.esprit.ecommerce.repository.ProductRepository;
import tn.esprit.ecommerce.entity.User;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public Cart addProductToCart(User user, String productId, int quantity) {
        // Ensure the cart exists or create a new one if not found
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));

        // Validate Product - ensure product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

        // Validate Product ID - it shouldn't be null
        if (product.getId() == null) {
            throw new IllegalStateException("Product ID cannot be null for product: " + product);
        }

        // Check if CartItem already exists, ensuring we are not dealing with null CartItems or Products
        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item != null && item.getProduct() != null && item.getProduct().getId().equals(productId))
                .findFirst();

        // Update existing item or add a new one
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity); // Increase quantity if item already exists
            cartItemRepository.save(item);
        } else {
            // Create a new CartItem if not found
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .build();

            // Validate new CartItem's Product ID
            if (newItem.getProduct() == null || newItem.getProduct().getId() == null) {
                throw new IllegalStateException("CartItem must reference a valid Product with a non-null ID");
            }

            cart.getCartItems().add(newItem); // Add the new item to the cart
            cartItemRepository.save(newItem);
        }

        // Save the updated cart
        return cartRepository.save(cart);
    }


    public Cart updateProductQuantity(User user, String productId, int newQuantity) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Article non trouvé dans le panier"));

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        cart.getTotal();
        return cartRepository.save(cart);
    }

    public Cart removeProductFromCart(User user, String productId) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Panier non trouvé"));

        CartItem item = cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Article non trouvé dans le panier"));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        cart.getTotal();
        return cartRepository.save(cart);
    }

    private Cart createNewCart(User user) {
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return cartRepository.save(cart);
    }




}