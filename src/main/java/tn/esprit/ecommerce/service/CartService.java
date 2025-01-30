package tn.esprit.ecommerce.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.CartItem;
import tn.esprit.ecommerce.entity.Product;
import tn.esprit.ecommerce.exception.CartNotFoundException;
import tn.esprit.ecommerce.exception.ProductNotFoundException;
import tn.esprit.ecommerce.repository.CartItemRepository;
import tn.esprit.ecommerce.repository.CartRepository;
import tn.esprit.ecommerce.repository.ProductRepository;
import tn.esprit.ecommerce.entity.User;

import java.util.ArrayList;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Adds a product to the authenticated user's cart.
     *
     * @param user       The authenticated user.
     * @param productId  The ID of the product to be added.
     * @param quantity   The quantity of the product to add.
     * @return The updated Cart after adding the product.
     * @throws ProductNotFoundException if the product is not found.
     * @throws CartNotFoundException    if the cart is not found.
     */
    @Transactional
    public Cart addProductToCart(User user, String productId, int quantity) {
        validateInput(user, productId, quantity);

        Cart cart = getOrCreateCart(user);
        Product product = getProductById(productId);

        Optional<CartItem> existingItem = findCartItemByProduct(cart, productId);

        if (existingItem.isPresent()) {
            updateCartItemQuantity(existingItem.get(), quantity);
        } else {
            CartItem newItem = createCartItem(product, quantity);
            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return cartRepository.save(cart);
    }

    /**
     * Updates the quantity of a product in the cart.
     *
     * @param user        The authenticated user.
     * @param productId   The ID of the product to update.
     * @param newQuantity The new quantity of the product.
     * @return The updated Cart.
     * @throws CartNotFoundException    if the cart is not found.
     * @throws ProductNotFoundException if the product is not found in the cart.
     */
    @Transactional
    public Cart updateProductQuantity(User user, String productId, int newQuantity) {
        validateInput(user, productId, newQuantity);

        Cart cart = getCartByUser(user);
        CartItem item = findCartItemByProduct(cart, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart: " + productId));

        item.setQuantity(newQuantity);
        cartItemRepository.save(item);

        cart.getTotal();
        return cartRepository.save(cart);
    }

    /**
     * Removes a product from the cart.
     *
     * @param user      The authenticated user.
     * @param productId The ID of the product to remove.
     * @return The updated Cart.
     * @throws CartNotFoundException    if the cart is not found.
     * @throws ProductNotFoundException if the product is not found in the cart.
     */
    @Transactional
    public Cart removeProductFromCart(User user, String productId) {
        validateInput(user, productId, 1); // Quantity is irrelevant here

        Cart cart = getCartByUser(user);
        CartItem item = findCartItemByProduct(cart, productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found in cart: " + productId));

        cart.getCartItems().remove(item);
        cartItemRepository.delete(item);

        cart.getTotal();
        return cartRepository.save(cart);
    }

    /**
     * Creates a new cart for the user.
     *
     * @param user The authenticated user.
     * @return The newly created Cart.
     */
    private Cart createNewCart(User user) {
        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setCartItems(new ArrayList<>());
        return cartRepository.save(newCart);
    }

    // Helper Methods

    private void validateInput(User user, String productId, int quantity) {
        if (user == null || productId == null || quantity <= 0) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
    }

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user).orElseGet(() -> createNewCart(user));
    }

    private Cart getCartByUser(User user) {
        return cartRepository.findByUser(user)
                .orElseThrow(() -> new CartNotFoundException("Cart not found for user: " + user.getId()));
    }

    private Product getProductById(String productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with ID: " + productId));
    }

    private Optional<CartItem> findCartItemByProduct(Cart cart, String productId) {
        return cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    private void updateCartItemQuantity(CartItem item, int quantity) {
        item.setQuantity(item.getQuantity() + quantity);
        cartItemRepository.save(item);
    }

    private CartItem createCartItem(Product product, int quantity) {
        return CartItem.builder()
                .product(product)
                .quantity(quantity)
                .build();
    }
}