package com.fardin.orderservice.message.listeners;

import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.models.OrderItem;
import com.fardin.orderservice.services.OrderService;
import com.fardin.orderservice.states.OrderStatus;
import com.fardin.orderservice.states.ValidationStatus;
import com.shopmate.events.CheckoutEvent;
import com.shopmate.events.ProductValidationEvent;
import com.shopmate.events.UserValidationEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CheckoutListener {
    @Autowired
    private OrderService orderService;
    @KafkaListener(topics = "checkout", groupId = "order-service-group")
    public void onCheckout(CheckoutEvent event) {
        orderService.createOrUpdateOrderFromCheckoutEvent(event.getId(), event,() -> {
            Order order = new Order();
            order.setCreatedAt(LocalDateTime.now());
            order.setUsername(event.getUsername());
            order.setCheckoutId(event.getId());
            order.setStatus(OrderStatus.PENDING);

            List<OrderItem> items = event.getCartItems().stream().map(cartItem -> {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setProductId(cartItem.getProductId().intValue());
                item.setQuantity(BigDecimal.valueOf(cartItem.getQuantity()));
                item.setPrice(new BigDecimal(cartItem.getPrice()));
                item.setTotalAmount(item.getPrice().multiply(item.getQuantity()));
                return orderService.saveOrderItem(item);
            }).toList();
            order.setOrderItems(items);
            return order;
        });
    }

    @KafkaListener(topics = "user-validation", groupId = "order-service-group")
    public void onUserValidation(UserValidationEvent event) {
        Optional<Order> optional = orderService.findByCheckoutId(event.getCheckoutId());
        if (optional.isPresent()) {
            Order order =  optional.get();
            order.setUpdatedAt(LocalDateTime.now());
            order.setUserValidationStatus(event.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
            orderService.save(order);
        }else{
            Order order = new Order();
            order.setUserValidationStatus(event.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
            order.setCheckoutId(event.getCheckoutId());
            order.setUsername(event.getUsername());
            orderService.save(order);
        }
    }

    @KafkaListener(topics = "product-validation", groupId = "order-service-group")
    public void onProductValidation(ProductValidationEvent event) {
        Optional<Order> optional = orderService.findByCheckoutId(event.getCheckoutId());
        if(optional.isPresent()){
            Order order =  optional.get();
            order.setOrderValidationStatus(event.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
            order.setUpdatedAt(LocalDateTime.now());
            orderService.save(order);
        }else{
            Order order = new Order();
            order.setOrderValidationStatus(event.isValid() ? ValidationStatus.VALID : ValidationStatus.INVALID);
            order.setCheckoutId(event.getCheckoutId());
            orderService.save(order);
        }

    }
}
