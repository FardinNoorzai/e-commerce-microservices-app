package com.fardin.orderservice.controllers;

import com.fardin.orderservice.dtos.OrderStatus;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/orders")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @GetMapping("{orderId}/status")
    public OrderStatus getOrderStatus(@PathVariable("orderId") String orderId) {
        return orderService.getOrderStatus(orderId);
    }
    @PostMapping
    public ResponseEntity<Map<String,Order>> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(Map.of("order",order));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
