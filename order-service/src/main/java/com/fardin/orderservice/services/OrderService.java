package com.fardin.orderservice.services;

import com.fardin.orderservice.feign.clients.ProductServiceClient;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.models.OrderItem;
import com.fardin.orderservice.repositories.OrderItemRepository;
import com.fardin.orderservice.repositories.OrderRepository;
import com.fardin.orderservice.states.OrderStatus;
import com.fardin.orderservice.states.ValidationStatus;
import com.shopmate.events.*;
import com.shopmate.states.InventoryStates;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
public class OrderService {

    @Autowired
    ProductServiceClient productServiceClient;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;


    public Order save(Order order) {
        return orderRepository.save(order);
    }
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
    public Order findById(String id){
        return orderRepository.findById(id).orElseThrow(() ->{ throw new RuntimeException("Order not found");} );
    }
    public void deleteById(String id){
        orderRepository.deleteById(id);
    }
    @Transactional
    public synchronized Order createOrUpdateOrderFromCheckoutEvent(String checkoutId,CheckoutEvent checkoutEvent, Supplier<Order> orderCreator) {
        return orderRepository.findByCheckoutId(checkoutId)
                .map(existingOrder -> {
                    for(CartItem cartItem : checkoutEvent.getCartItems()){
                        OrderItem orderItem = new OrderItem();
                        orderItem.setOrder(existingOrder);
                        orderItem.setPrice(new BigDecimal(cartItem.getPrice()));
                        orderItem.setQuantity(new BigDecimal(cartItem.getQuantity()));
                        orderItem.setProductId(cartItem.getProductId().intValue());
                        orderItem.setTotalAmount(new BigDecimal(cartItem.getPrice()).multiply(new BigDecimal(cartItem.getQuantity())));
                        orderItemRepository.save(orderItem);
                    }
                    if(existingOrder.getOrderValidationStatus() == ValidationStatus.VALID && existingOrder.getUserValidationStatus() == ValidationStatus.VALID){

                    }
                    return orderRepository.save(existingOrder);
                })
                .orElseGet(() -> {
                    Order order = orderCreator.get();
                    return orderRepository.save(order);
                });
    }


    public Order createOrderFromCheckoutEvent(CheckoutEvent checkoutEvent){
        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setUsername(checkoutEvent.getUsername());
        order.setCheckoutId(checkoutEvent.getId());
        order = orderRepository.save(order);
        for(CartItem cartItem : checkoutEvent.getCartItems()){
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPrice(new BigDecimal(cartItem.getPrice()));
            orderItem.setQuantity(new BigDecimal(cartItem.getQuantity()));
            orderItem.setProductId(cartItem.getProductId().intValue());
            orderItem.setTotalAmount(new BigDecimal(cartItem.getPrice()).multiply(new BigDecimal(cartItem.getQuantity())));
            orderItemRepository.save(orderItem);
        }
        return order;
    }

    public com.fardin.orderservice.dtos.OrderStatus getOrderStatus(String orderId) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if(order == null){
            return null;
        }
        com.fardin.orderservice.dtos.OrderStatus orderStatus = new com.fardin.orderservice.dtos.OrderStatus();
        orderStatus.setOrderId(order.getId());
        orderStatus.setStatus(order.getStatus().toString());
        orderStatus.setPaymentUrl(order.getPaymentUrl());
        return orderStatus;
    }

    public Optional<Order> findByCheckoutId(String checkoutId) {
        return orderRepository.findByCheckoutId(checkoutId);
    }
}
