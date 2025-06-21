package com.fardin.inventroyservice.models;

import com.shopmate.states.InventoryStates;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
public class ProductEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    String username;
    BigInteger quantity;
    Integer productId;

    public ProductEntry() {

    }

    public ProductEntry(String id, String username, BigInteger quantity, Integer productId) {
        this.id = id;
        this.username = username;
        this.quantity = quantity;
        this.productId = productId;
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

    public BigInteger getQuantity() {
        return quantity;
    }

    public void setQuantity(BigInteger quantity) {
        this.quantity = quantity;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
