package com.shopmate.cartservice.services;

import com.shopmate.cartservice.dtos.CheckoutResponse;
import com.shopmate.cartservice.exceptions.CartEmptyException;
import com.shopmate.cartservice.models.*;
import com.shopmate.cartservice.dtos.CartItemRequest;
import com.shopmate.cartservice.dtos.CartResponse;
import com.shopmate.cartservice.repositories.CartItemRepository;
import com.shopmate.cartservice.repositories.CartRepository;
import com.shopmate.cartservice.repositories.CheckoutRepository;
import com.shopmate.events.CheckoutEvent;
import jakarta.transaction.Transactional;
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
    @Autowired
    private CheckoutRepository checkoutRepository;

    @Autowired
    CheckoutPublisher checkoutPublisher;

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

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()) &&
                        item.getStatus() == CartItemStatus.ACTIVE)
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
        } else {
            CartItem newItem = new CartItem();
            newItem.setProductId(request.getProductId());
            newItem.setProductName(request.getProductName());
            newItem.setProductPrice(request.getProductPrice());
            newItem.setQuantity(request.getQuantity());
            newItem.setCart(cart);
            newItem.setStatus(CartItemStatus.ACTIVE);
            cart.getItems().add(newItem);
        }

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

    @Transactional
    public CheckoutResponse checkout(String userId) {
        Cart cart = getOrCreateCart(userId);
        List<CartItem> activeItems = cart.getItems().stream()
                .filter(item -> item.getStatus() == CartItemStatus.ACTIVE)
                .toList();

        if (activeItems.isEmpty()) {
            throw new CartEmptyException("Cart is empty");
        }

        activeItems.forEach(item -> item.setStatus(CartItemStatus.PENDING));

        Checkout checkout = new Checkout();
        checkout.setUserId(userId);
        checkout.setItems(activeItems);

        activeItems.forEach(item -> item.setCheckout(checkout));
        checkout.setStatus(CheckoutStatus.PENDING);
        Checkout savedCheckout = checkoutRepository.save(checkout);

        List<com.shopmate.events.CartItem> kafkaItems = activeItems.stream()
                .map(item -> {
                    com.shopmate.events.CartItem dto = new com.shopmate.events.CartItem();
                    dto.setProductId(item.getProductId());
                    dto.setQuantity((long) item.getQuantity());
                    dto.setPrice(item.getProductPrice().toString());
                    return dto;
                }).toList();

        CheckoutEvent checkoutDto = new CheckoutEvent();
        checkoutDto.setId(savedCheckout.getId());
        checkoutDto.setUsername(savedCheckout.getUserId());
        checkoutDto.setCartItems(kafkaItems);
        System.out.println("Publishing to kafka topic");
        checkoutPublisher.publish(checkoutDto);
        CheckoutResponse response = new CheckoutResponse();
        response.setUrl("/api/orders/" + savedCheckout.getId());
        response.setCheckout(savedCheckout);

        return response;
    }

    public List<Checkout> getCheckouts(String userId) {
        return checkoutRepository.findAllByUserId(userId);
    }



}
