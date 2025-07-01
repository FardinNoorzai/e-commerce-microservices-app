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
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    @Column(unique = true)
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
    @Enumerated(EnumType.STRING)
    ValidationStatus inventoryValidationStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order")
    List<OrderItem> orderItems;
    BigDecimal totalPrice;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public ValidationStatus getUserValidationStatus() {
        return userValidationStatus;
    }

    public void setUserValidationStatus(ValidationStatus userValidationStatus) {
        this.userValidationStatus = userValidationStatus;
    }

    public ValidationStatus getOrderValidationStatus() {
        return orderValidationStatus;
    }

    public void setOrderValidationStatus(ValidationStatus orderValidationStatus) {
        this.orderValidationStatus = orderValidationStatus;
    }

    public ValidationStatus getInventoryValidationStatus() {
        return inventoryValidationStatus;
    }

    public void setInventoryValidationStatus(ValidationStatus inventoryValidationStatus) {
        this.inventoryValidationStatus = inventoryValidationStatus;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getShippedAt() {
        return shippedAt;
    }

    public void setShippedAt(LocalDateTime shippedAt) {
        this.shippedAt = shippedAt;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", checkoutId='" + checkoutId + '\'' +
                ", status=" + status +
                ", paymentId='" + paymentId + '\'' +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", userValidationStatus=" + userValidationStatus +
                ", orderValidationStatus=" + orderValidationStatus +
                ", inventoryValidationStatus=" + inventoryValidationStatus +
                ", orderItems=" + orderItems +
                ", totalPrice=" + totalPrice +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", shippedAt=" + shippedAt +
                ", shippingAddress=" + shippingAddress +
                '}';
    }
}
