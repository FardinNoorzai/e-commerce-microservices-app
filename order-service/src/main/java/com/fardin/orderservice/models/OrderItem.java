package com.fardin.orderservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    private BigDecimal totalAmount;
    private BigDecimal quantity;
    private BigDecimal price;
    private Integer productId;
    @ManyToOne
    private Order order;
}
