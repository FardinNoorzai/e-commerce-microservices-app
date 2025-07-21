package com.fardin.orderservice.services;

import com.fardin.orderservice.events.OrderCreatedEvent;
import com.fardin.orderservice.models.OrderItem;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.events.CartItem;
import com.shopmate.events.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderCreationListener {

    @Autowired
    private OrderPublisher orderPublisher;

    @EventListener(OrderCreatedEvent.class)
    public void onOrderCreation(OrderCreatedEvent orderCreatedEvent) {
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(orderCreatedEvent.getOrder().getId());
        orderEvent.setCheckoutId(orderCreatedEvent.getOrder().getCheckoutId());
        orderEvent.setTotalPrice(orderCreatedEvent.getOrder().getTotalPrice());
        List<CartItem> items = new ArrayList<>();
        if (orderCreatedEvent.getOrder().getOrderItems() != null) {
            for (OrderItem item : orderCreatedEvent.getOrder().getOrderItems()) {
                CartItem cartItem = new CartItem();
                cartItem.setPrice(item.getPrice().toString());
                cartItem.setProductId(item.getProductId().longValue());
                cartItem.setQuantity(item.getQuantity().longValue());
                items.add(cartItem);
            }
        }
        orderEvent.setCartItems(items);
        if (orderCreatedEvent.getOrder().getStatus().equals(OrderStatus.PENDING)) {
            orderEvent.setStatus(OrderEvent.OrderStatus.VALID);
        } else if (orderCreatedEvent.getOrder().getStatus().equals(OrderStatus.FAILED)) {
            orderEvent.setStatus(OrderEvent.OrderStatus.FAILED);
        }
        orderPublisher.publish(orderEvent);
    }
}
