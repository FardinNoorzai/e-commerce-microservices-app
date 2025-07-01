package com.fardin.orderservice.services;

import com.fardin.orderservice.events.OrderCreatedEvent;
import com.fardin.orderservice.feign.clients.ProductServiceClient;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.models.OrderItem;
import com.fardin.orderservice.repositories.OrderItemRepository;
import com.fardin.orderservice.repositories.OrderRepository;
import com.fardin.orderservice.states.OrderStatus;
import com.fardin.orderservice.states.ValidationStatus;
import com.shopmate.events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    ProductServiceClient productServiceClient;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    OrderItemRepository orderItemRepository;
    @Autowired
    ApplicationEventPublisher applicationEventPublisher;


    public Order save(Order order) {
        return orderRepository.save(order);
    }
    public OrderItem saveOrderItem(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public Order findByCheckoutId(String id) {
        return orderRepository.findOrderByCheckoutId(id).orElse(null);
    }

    public Order findById(String id){
        return orderRepository.findById(id).orElseThrow(() ->{ throw new RuntimeException("Order not found");} );
    }
    public void deleteById(String id){
        orderRepository.deleteById(id);
    }


    public com.fardin.orderservice.dtos.OrderStatus getOrderStatus(String checkoutId) {
        Order order = findByCheckoutId(checkoutId);
        if(order == null){
            return null;
        }
        com.fardin.orderservice.dtos.OrderStatus orderStatus = new com.fardin.orderservice.dtos.OrderStatus();
        orderStatus.setCheckoutId(order.getCheckoutId());
        orderStatus.setOrderId(order.getId());
        orderStatus.setOrderStatus(order.getStatus().toString());
        orderStatus.setPaymentUrl(order.getPaymentUrl());
        return orderStatus;
    }
    public void createOrder(CheckoutEvent checkout, UserValidationEvent user, ProductValidationEvent product,InventoryValidationEvent inventory) {
        System.out.println("Create order were called "+ checkout.getId());
        BigDecimal totalPrice = BigDecimal.ZERO;
        Order order = new Order();
        order.setCheckoutId(checkout.getId());
        order.setCreatedAt(LocalDateTime.now());
        order.setUsername(user.getUsername());
        order.setOrderValidationStatus(product.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
        order.setUserValidationStatus(user.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
        order.setInventoryValidationStatus(inventory.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
        order.setStatus((user.isValid() && product.isValid() && inventory.isValid())? OrderStatus.PENDING : OrderStatus.FAILED);


        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : checkout.getCartItems()) {
            BigDecimal price = new BigDecimal(cartItem.getPrice());
            BigDecimal quantity = new BigDecimal(cartItem.getQuantity());
            BigDecimal total = price.multiply(quantity);
            totalPrice = totalPrice.add(total);
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId().intValue());
            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            orderItem.setTotalAmount(total);

            orderItems.add(orderItem);
        }
        order.setTotalPrice(totalPrice);
        order = orderRepository.save(order);
        orderItems = orderItemRepository.saveAll(orderItems);
        order.setOrderItems(orderItems);
        applicationEventPublisher.publishEvent(new OrderCreatedEvent(this,order));
    }
    public void updateOrderWithPaymentInformation(PaymentEvent paymentEvent) {
        String checkoutId = paymentEvent.getCheckoutId();
        System.out.println(checkoutId);
        Order order = findByCheckoutId(checkoutId);
        order.setPaymentId(paymentEvent.getPaymentId());
        order.setPaymentUrl(paymentEvent.getUri());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        orderRepository.save(order);
    }
    public void createFailedOrder(String checkoutId) {
        Order order = new Order();
        order.setStatus(OrderStatus.FAILED);
        order.setCreatedAt(LocalDateTime.now());
        order.setCheckoutId(checkoutId);
        order = orderRepository.save(order);
        applicationEventPublisher.publishEvent(new OrderCreatedEvent(this,order));
    }
}
