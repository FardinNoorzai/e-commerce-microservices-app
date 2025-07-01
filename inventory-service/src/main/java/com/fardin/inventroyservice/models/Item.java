package com.fardin.inventroyservice.models;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    Integer productId;
    BigDecimal quantity;
    @ManyToOne
    Transaction transaction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", transaction=" + transaction +
                '}';
    }
}
