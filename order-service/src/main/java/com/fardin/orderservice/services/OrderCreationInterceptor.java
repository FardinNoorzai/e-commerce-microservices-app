package com.fardin.orderservice.services;

import com.fardin.orderservice.converters.OrderConvertor;
import com.fardin.orderservice.message.publishers.NewOrderPublishers;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.OrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@RepositoryEventHandler(Order.class)
@Component
public class OrderCreationInterceptor {
    @Autowired
    NewOrderPublishers newOrderPublishers;
    @Autowired
    OrderConvertor orderConvertor;
    @HandleBeforeCreate
    public void handleBeforeCreate(Order order) {
        order.setCreatedAt(LocalDateTime.now());
        System.out.println(order.getPrice().toString() + " * " + order.getQuantity() + " = " + order.getPrice().multiply(order.getQuantity()));
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(order.getPrice().multiply(order.getQuantity()));
    }
    @HandleAfterCreate
    public void handleAfterCreate(Order order) {
        com.shopmate.OrderDto dto = orderConvertor.convert(order);
        newOrderPublishers.publishNewOrder("order",dto);
    }
}
