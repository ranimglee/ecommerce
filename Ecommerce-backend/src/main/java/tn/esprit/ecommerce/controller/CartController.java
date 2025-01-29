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
        User user = getAuthenticatedUser(); // Get the authenticated user
        if (user == null) {
            throw new RuntimeException("User not found"); // Throw error if user not found
        }

        // Validate productId and quantity from request
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("The productId is required"); // Validate productId is not empty
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero"); // Validate quantity is positive
        }

        try {
            // Call the service to add the product to the cart
            Cart updatedCart = cartService.addProductToCart(user, productId, quantity);
            if (updatedCart == null) {
                throw new RuntimeException("Failed to add product to cart"); // Handle failure in adding to cart
            }
            return updatedCart; // Return the updated cart
        } catch (Exception e) {
            // Log error and rethrow the exception with a message
            System.err.println("Error adding product to cart: " + e.getMessage());
            throw new RuntimeException("Error adding product to cart", e);
        }
    }

    @PutMapping("/update-cart")
    public Cart updateProductQuantity(@RequestParam String productId, @RequestParam int quantity) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return cartService.updateProductQuantity(user, productId, quantity);
    }

   @DeleteMapping("/remove-from-cart")
    public Cart removeProductFromCart(@RequestParam String productId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return cartService.removeProductFromCart(user, productId);
    }


    // Method to retrieve the authenticated user
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        return userService.findByUsername(username);
    }
    @GetMapping("/view-cart")
    public ResponseEntity<Cart> viewCart(@AuthenticationPrincipal User loggedInUser) {
        Cart cart = cartRepository.findByUser(loggedInUser)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        return ResponseEntity.ok(cart);
    }

}
