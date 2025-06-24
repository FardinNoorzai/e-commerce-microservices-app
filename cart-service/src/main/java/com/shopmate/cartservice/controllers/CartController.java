package com.shopmate.cartservice.controllers;

import com.shopmate.cartservice.dtos.CartItemRequest;
import com.shopmate.cartservice.dtos.CartResponse;
import com.shopmate.cartservice.dtos.CheckoutResponse;
import com.shopmate.cartservice.models.Cart;
import com.shopmate.cartservice.models.CartItem;
import com.shopmate.cartservice.models.CartItemStatus;
import com.shopmate.cartservice.models.Checkout;
import com.shopmate.cartservice.repositories.CheckoutRepository;
import com.shopmate.cartservice.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    CheckoutRepository checkoutRepository;

    @PostMapping
    public ResponseEntity<Map<String, List<CartItem>>> addItemToCart(
            @RequestBody CartItemRequest request, Authentication authentication
    ) {
        Cart cart = cartService.addItem(authentication.getName(), request);
        return ResponseEntity.ok(Map.of("cart", cart.getItems().stream().filter((item)->{
            return item.getStatus() == CartItemStatus.ACTIVE;
        }).toList()));
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        CartResponse cart = cartService.getCart(authentication.getName());
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<Map<String,String>> removeItem(@PathVariable Long itemId, Authentication authentication) {
        boolean res = cartService.removeItem(itemId,authentication.getName());
        if(res){
            return ResponseEntity.ok(Map.of("message", "Item deleted from cart."));
        }
        return ResponseEntity.status(403).body(Map.of("message", "You are not allowed to delete this item."));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Map<String,String>> clearCart(Authentication authentication) {
        cartService.clearCart(authentication.getName());
        return ResponseEntity.ok(Map.of("message","Cart cleared."));
    }
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutResponse> checkout(Authentication authentication) {
        return ResponseEntity.ok(cartService.checkout(authentication.getName()));
    }
}
