package com.fardin.inventroyservice.services;

import com.fardin.inventroyservice.models.Transaction;
import com.fardin.inventroyservice.repository.ProductEntryRepository;
import com.fardin.inventroyservice.repository.TransactionRepository;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.OrderDto;
import com.shopmate.states.InventoryStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InventoryService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ProductEntryRepository productEntryRepository;
    public InventoryResponseToNewOrderDto calculateInventory(OrderDto orderDto) {
        BigDecimal entries = productEntryRepository.calculateInventoryByProductId(orderDto.getProductId()).orElse(BigDecimal.ZERO);
        BigDecimal transactions = transactionRepository.calculateInventoryByProductIdAndNotRejected(orderDto.getProductId()).orElse(BigDecimal.ZERO);
        BigDecimal total = entries.subtract(transactions);
        System.out.println(entries.toString());
        System.out.println(transactions.toString());
        if(total.compareTo(orderDto.getQuantity()) >= 0){
            Transaction transaction = Transaction.builder()
                    .state(InventoryStates.HOLD)
                    .orderId(orderDto.getOrderId())
                    .productId(orderDto.getProductId())
                    .quantity(orderDto.getQuantity())
                    .username(orderDto.getUserName())
                    .build();
            transaction = transactionRepository.save(transaction);
            InventoryResponseToNewOrderDto response = InventoryResponseToNewOrderDto.builder()
                    .inventoryId(transaction.getId())
                    .inventoryState(transaction.getState())
                    .orderId(transaction.getOrderId())
                    .productId(transaction.getProductId())
                    .quantity(transaction.getQuantity())
                    .userName(transaction.getUsername())
                    .price(orderDto.getPrice())
                    .totalPrice(orderDto.getTotalPrice())
                    .build();
            return response;
        }
        return InventoryResponseToNewOrderDto.builder()
                .inventoryState(InventoryStates.REJECTED)
                .orderId(orderDto.getOrderId())
                .productId(orderDto.getProductId())
                .quantity(orderDto.getQuantity())
                .userName(orderDto.getUserName())
                .price(orderDto.getPrice())
                .totalPrice(orderDto.getTotalPrice())
                .build();
    }
}
