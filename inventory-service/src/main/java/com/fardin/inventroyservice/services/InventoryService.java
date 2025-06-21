package com.fardin.inventroyservice.services;

import com.fardin.inventroyservice.models.Transaction;
import com.fardin.inventroyservice.repository.ProductEntryRepository;
import com.fardin.inventroyservice.repository.TransactionRepository;
import com.shopmate.dtos.InventoryResponseToNewOrderDto;
import com.shopmate.dtos.OrderDto;
import com.shopmate.dtos.PaymentSuccessDto;
import com.shopmate.states.InventoryStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Service
public class InventoryService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ProductEntryRepository productEntryRepository;
    public InventoryResponseToNewOrderDto calculateInventory(OrderDto orderDto) {
        BigInteger entries = productEntryRepository.calculateInventoryByProductId(orderDto.getProductId()).orElse(BigInteger.ZERO);
        BigInteger transactions = transactionRepository.calculateInventoryByProductIdAndNotRejected(orderDto.getProductId()).orElse(BigInteger.ZERO);
        BigInteger total = entries.subtract(transactions);
        System.out.println("entries: " + entries);
        System.out.println("transactions: " + transactions);
        System.out.println("total: " + total);
        if(total.compareTo(orderDto.getQuantity()) >= 0){
            Transaction transaction = new Transaction();
            transaction.setProductId(orderDto.getProductId());
            transaction.setQuantity(orderDto.getQuantity());
            transaction.setState(InventoryStates.HOLD);
            transaction.setOrderId(orderDto.getOrderId());
            transaction.setTimestamp(LocalDateTime.now());
            transaction.setUsername(orderDto.getUserName());
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

    public void updateOrderStatus(PaymentSuccessDto message) {
        Transaction t = transactionRepository.findByOrderId(message.getOrderId()).orElse(null);
        if(t == null){
            return;
        }
        t.setState(InventoryStates.COMPLETED);
        transactionRepository.save(t);
    }
}
