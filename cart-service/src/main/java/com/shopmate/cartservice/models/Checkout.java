package com.shopmate.cartservice.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Checkout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "checkout", cascade = CascadeType.ALL)
    private List<CartItem> items;
    @Enumerated(EnumType.STRING)
    CheckoutStatus status;
    public Checkout() {
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public CheckoutStatus getStatus() {
        return status;
    }

    public void setStatus(CheckoutStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Checkout{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", createdAt=" + createdAt +
                ", items=" + items +
                '}';
    }
}
