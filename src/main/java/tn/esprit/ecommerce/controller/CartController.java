package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.ecommerce.repository.CartRepository;
import tn.esprit.ecommerce.service.CartService;
import tn.esprit.ecommerce.entity.Cart;
import tn.esprit.ecommerce.entity.User;
import tn.esprit.ecommerce.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;
    private final CartRepository cartRepository;


    @PostMapping("/add-to-cart")
    public Cart addProductToCart(@RequestParam String productId, @RequestParam int quantity) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Validate request parameters
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("The productId is required");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        try {
            Cart updatedCart = cartService.addProductToCart(user, productId, quantity);
            if (updatedCart == null) {
                throw new RuntimeException("Failed to add product to cart");
            }
            return updatedCart;
        } catch (Exception e) {
            System.err.println("Error adding product to cart: " + e.getMessage());
            throw new RuntimeException("Error adding product to cart", e);
        }
    }

    /**
     * Updates the quantity of a specific product in the authenticated user's cart.
     *
     * @param productId The ID of the product to update.
     * @param quantity The new quantity of the product.
     * @return The updated Cart after modifying the quantity.
     * @throws RuntimeException if the user is not authenticated.
     */
    @PutMapping("/update-cart")
    public Cart updateProductQuantity(@RequestParam String productId, @RequestParam int quantity) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return cartService.updateProductQuantity(user, productId, quantity);
    }

    /**
     * Removes a product from the authenticated user's cart.
     *
     * @param productId The ID of the product to remove.
     * @return The updated Cart after removing the product.
     * @throws RuntimeException if the user is not authenticated.
     */
    @DeleteMapping("/remove-from-cart")
    public Cart removeProductFromCart(@RequestParam String productId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return cartService.removeProductFromCart(user, productId);
    }

    /**
     * Retrieves the authenticated user from the security context.
     *
     * @return The authenticated User object, or null if authentication fails.
     */
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userService.findByUsername(username);
    }

    /**
     * Retrieves the authenticated user's cart.
     *
     * @param loggedInUser The currently authenticated user.
     * @return ResponseEntity containing the user's Cart.
     * @throws RuntimeException if the cart is not found.
     */
    @GetMapping("/view-cart")
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal User loggedInUser) {
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return ResponseEntity.ok(cart);
    }
}
