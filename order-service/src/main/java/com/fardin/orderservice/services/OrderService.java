package com.fardin.orderservice.services;

import com.fardin.orderservice.feign.clients.ProductServiceClient;
import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.repositories.OrderRepository;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.dtos.*;
import com.shopmate.states.InventoryStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    ProductServiceClient productServiceClient;
    @Autowired
    OrderRepository orderRepository;
    public Order updateOrderStatusByInventoryResponse(InventoryResponseToNewOrderDto inventoryResponse) {
        System.out.println("update order status was called");
        Order order = orderRepository.findById(inventoryResponse.getOrderId()).orElse(null);
        if(order == null){
            return null;
        }
        if(inventoryResponse.getInventoryState() == InventoryStates.REJECTED){
            order.setStatus(OrderStatus.REJECTED_BY_INVENTORY);
        }
        if(inventoryResponse.getInventoryState() == InventoryStates.HOLD){
            order.setStatus(OrderStatus.PAYMENT_PENDING);
        }
        order.setUpdatedAt(LocalDateTime.now());
        order.setInventoryStates(inventoryResponse.getInventoryState());
        return orderRepository.save(order);

    }
    public Order updateOrderStatusByPaymentResponse(PaymentDto paymentDto) {
        Order order = orderRepository.findById(paymentDto.getOrderId()).orElse(null);
        if(order == null){
            return null;
        }
        if(paymentDto.getUri() == null){
            order.setStatus(OrderStatus.REJECTED_BY_PAYMENT);
        }
        order.setPaymentUrl(paymentDto.getUri());
        order.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
    public void updateOrderByPaymentCompletion(CompletedPaymentDto dto){
        Order order = orderRepository.findById(dto.getOrderId()).orElse(null);
        if(order == null){
            return;
        }
        order.setStatus(OrderStatus.SHIPPING);
        order.setUpdatedAt(LocalDateTime.now());
        order.setPaymentId(dto.getPaymentId());
        order.setInventoryStates(InventoryStates.COMPLETED);
        orderRepository.save(order);
    }

    public CheckoutSessionRequestDto createCheckoutSessionObject(Order order){
        System.out.println("create checkout session object was called");
        ProductDto productDto = productServiceClient.getProduct(order.getProductId().toString());
        System.out.println(productDto);
        CheckoutSessionRequestDto checkoutSessionRequestDto = new CheckoutSessionRequestDto();
        checkoutSessionRequestDto.setOrderId(order.getId());
        checkoutSessionRequestDto.setAmount(order.getQuantity());
        checkoutSessionRequestDto.setPrice(order.getPrice());
        checkoutSessionRequestDto.setCancelUrl("http://localhost:8080/checkout");
        checkoutSessionRequestDto.setSuccessUrl("http://localhost:8080/checkout");
        checkoutSessionRequestDto.setProduct(productDto.getName());
        checkoutSessionRequestDto.setCurrency("usd");
        return checkoutSessionRequestDto;
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

}
