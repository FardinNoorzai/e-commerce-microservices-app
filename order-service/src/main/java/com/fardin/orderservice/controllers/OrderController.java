package com.fardin.orderservice.controllers;

import com.fardin.orderservice.dtos.OrderStatus;
import com.fardin.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("{orderId}/status")
    public OrderStatus getOrderStatus(@PathVariable("orderId") String orderId) {
        return orderService.getOrderStatus(orderId);
    }
}
