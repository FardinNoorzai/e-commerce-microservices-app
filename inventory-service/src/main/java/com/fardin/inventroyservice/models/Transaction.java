package com.fardin.inventroyservice.models;

import com.shopmate.states.InventoryStates;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    String username;
    String checkoutId;
    @Enumerated(EnumType.STRING)
    InventoryStates state;
    LocalDateTime timestamp;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "transaction")
    List<Item> items;
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

    public InventoryStates getState() {
        return state;
    }

    public void setState(InventoryStates state) {
        this.state = state;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", checkoutId='" + checkoutId + '\'' +
                ", state=" + state +
                ", timestamp=" + timestamp +
                ", items=" + items +
                '}';
    }
}
