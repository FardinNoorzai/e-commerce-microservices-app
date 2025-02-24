package com.fardin.orderservice.services;

import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.repositories.OrderRepository;
import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.states.InventoryStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    public boolean updateOrderStatus(InventoryResponseToNewOrderDto inventoryResponse) {
        Order order = orderRepository.findById(inventoryResponse.getOrderId()).orElse(null);
        if(order == null){
            return false;
        }
        if(inventoryResponse.getInventoryState() == InventoryStates.REJECTED){
            order.setStatus(OrderStatus.REJECTED_BY_INVENTORY);
        }
        if(inventoryResponse.getInventoryState() == InventoryStates.HOLD){
            order.setStatus(OrderStatus.PAYMENT_PENDING);
        }
        order.setUpdatedAt(LocalDateTime.now());
        order.setInventoryStates(inventoryResponse.getInventoryState());
        orderRepository.save(order);
        return true;
    }

}
