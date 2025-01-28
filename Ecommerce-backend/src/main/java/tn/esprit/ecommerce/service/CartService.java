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

    // Ajouter un produit au panier
    public Cart addProductToCart(User user, String productId, int quantity) {

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> createNewCart(user));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));

        Optional<CartItem> existingItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = CartItem.builder()
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.getTotal();
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