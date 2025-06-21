package com.shopmate.shippingservice.models;

import jakarta.persistence.*;

@Entity
public class Ship {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Id
    String id;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name="street", column=@Column(name="billing_street")),
            @AttributeOverride(name="city", column=@Column(name="billing_city")),
            @AttributeOverride(name="state", column=@Column(name="billing_state")),
            @AttributeOverride(name="zipCode", column=@Column(name="billing_zip_code"))
    })
    private Address shippingAddress;
    String orderId;
    String username;
    Status status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
