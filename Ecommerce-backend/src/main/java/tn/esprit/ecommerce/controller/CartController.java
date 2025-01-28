package tn.esprit.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping("/add-to-cart")
    public Cart addProductToCart(@RequestParam String productId, @RequestParam int quantity) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }

        // Validate productId and quantity
        if (productId == null || productId.isEmpty()) {
            throw new IllegalArgumentException("Le productId est requis");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être supérieure à zéro");
        }

        try {
            Cart updatedCart = cartService.addProductToCart(user, productId, quantity);
            if (updatedCart == null) {
                throw new RuntimeException("Échec de l'ajout du produit au panier");
            }
            return updatedCart;
        } catch (Exception e) {
            // Log the exception for debugging
            // TODO: Replace System.err with a proper logging mechanism (e.g., SLF4J or Logback).
            System.err.println("Erreur lors de l'ajout du produit au panier: " + e.getMessage());
            throw new RuntimeException("Erreur lors de l'ajout du produit au panier", e);
        }
    }


    // Mettre à jour la quantité d'un produit
    @PutMapping("/update-cart")
    public Cart updateProductQuantity(@RequestParam String productId, @RequestParam int quantity) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        // TODO: Validate productId and quantity, similar to addProductToCart.
        return cartService.updateProductQuantity(user, productId, quantity);
    }

    @DeleteMapping("/remove-from-cart")
    public Cart removeProductFromCart(@RequestParam String productId) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new RuntimeException("Utilisateur non trouvé");
        }
        // TODO: Validate productId to ensure it is not null or empty.
        return cartService.removeProductFromCart(user, productId);
    }

    // Méthode pour récupérer l'utilisateur authentifié
    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        String username = authentication.getName();
        // TODO: Handle potential null return from userService.findByUsername.
        return userService.findByUsername(username);
    }
}
