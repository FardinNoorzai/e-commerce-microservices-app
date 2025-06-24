package com.fardin.orderservice.models;

import com.fardin.orderservice.states.OrderStatus;
import com.fardin.orderservice.states.ValidationStatus;
import com.shopmate.states.InventoryStates;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")

@Data
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private String checkoutId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    String paymentId;
    @Column(length = 512)
    String paymentUrl;
    @Enumerated(EnumType.STRING)
    ValidationStatus userValidationStatus;
    @Enumerated(EnumType.STRING)
    ValidationStatus orderValidationStatus;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    List<OrderItem> orderItems;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime shippedAt;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="street", column=@Column(name="billing_street")),
            @AttributeOverride(name="city", column=@Column(name="billing_city")),
            @AttributeOverride(name="state", column=@Column(name="billing_state")),
            @AttributeOverride(name="zipCode", column=@Column(name="billing_zip_code"))
    })
    private Address shippingAddress;

    public Order() {

    }
}
