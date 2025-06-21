package com.shopmate.cartservice.services;

import com.shopmate.cartservice.models.Cart;
import com.shopmate.cartservice.models.CartItem;
import com.shopmate.cartservice.dtos.CartItemRequest;
import com.shopmate.cartservice.dtos.CartResponse;
import com.shopmate.cartservice.models.CartItemStatus;
import com.shopmate.cartservice.repositories.CartItemRepository;
import com.shopmate.cartservice.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;


    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart cart = new Cart();
                    cart.setUserId(userId);
                    return cartRepository.save(cart);
                });
    }

    public Cart addItem(String userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = new CartItem();
        item.setProductId(request.getProductId());
        item.setProductName(request.getProductName());
        item.setProductPrice(request.getProductPrice());
        item.setQuantity(request.getQuantity());
        item.setCart(cart);
        item.setStatus(CartItemStatus.ACTIVE);

        cart.getItems().add(item);
        return cartRepository.save(cart);
    }

    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        CartResponse response = new CartResponse();
        response.setUserId(cart.getUserId());

        List<CartResponse.CartItemResponse> itemResponses = cart.getItems().stream().filter((item)->{
            return item.getStatus() == CartItemStatus.ACTIVE;
        }).map(item -> {
            CartResponse.CartItemResponse res = new CartResponse.CartItemResponse();
            res.setId(item.getId());
            res.setProductId(item.getProductId());
            res.setProductName(item.getProductName());
            res.setProductPrice(item.getProductPrice());
            res.setQuantity(item.getQuantity());
            return res;
        }).toList();

        response.setItems(itemResponses);
        return response;
    }

    public boolean removeItem(Long itemId, String userId) {
        Optional<CartItem> optionalItem = cartItemRepository.findById(itemId);
        if (optionalItem.isPresent()) {
            CartItem item = optionalItem.get();
            if (item.getCart().getUserId().equals(userId)) {
                item.setStatus(CartItemStatus.CANCELLED);
                cartItemRepository.save(item);
                return true;
            }
        }
        return false;
    }


    public void clearCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().forEach(item -> {
            item.setStatus(CartItemStatus.CANCELLED);
            cartItemRepository.save(item);
        });
    }

}
