package com.fardin.orderservice.models;

import com.fardin.orderservice.states.OrderStatus;
import com.shopmate.states.InventoryStates;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String username;
    private BigInteger totalAmount;
    private BigInteger quantity;
    private BigInteger price;
    private Integer productId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Enumerated(EnumType.STRING)
    private InventoryStates inventoryStates;

    @Column(length = 512)
    String paymentUrl;


    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="street", column=@Column(name="billing_street")),
            @AttributeOverride(name="city", column=@Column(name="billing_city")),
            @AttributeOverride(name="state", column=@Column(name="billing_state")),
            @AttributeOverride(name="zipCode", column=@Column(name="billing_zip_code"))
    })
    private Address shippingAddress;

    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime shippedAt;

    public Order() {
    }

    public Order(String id, String username, BigInteger totalAmount, BigInteger quantity, BigInteger price, Integer productId, OrderStatus status, InventoryStates inventoryStates, String paymentUrl, Address shippingAddress, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime shippedAt) {
        this.id = id;
        this.username = username;
        this.totalAmount = totalAmount;
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
        this.status = status;
        this.inventoryStates = inventoryStates;
        this.paymentUrl = paymentUrl;
        this.shippingAddress = shippingAddress;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.shippedAt = shippedAt;
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

    public BigInteger getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigInteger totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    public BigInteger getPrice() {
        return price;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public InventoryStates getInventoryStates() {
        return inventoryStates;
    }

    public void setInventoryStates(InventoryStates inventoryStates) {
        this.inventoryStates = inventoryStates;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
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

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }


    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", totalAmount=" + totalAmount +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productId=" + productId +
                ", status=" + status +
                ", inventoryStates=" + inventoryStates +
                ", paymentUrl='" + paymentUrl + '\'' +
                ", shippingAddress=" + shippingAddress +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", shippedAt=" + shippedAt +
                '}';
    }
}
