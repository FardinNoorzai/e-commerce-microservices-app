package com.fardin.inventroyservice.services;

import java.math.BigInteger;

public class InventoryTotal {
    private Integer productId;
    private BigInteger total;

    public InventoryTotal(Integer productId, BigInteger total) {
        this.productId = productId;
        this.total = total == null ? BigInteger.ZERO : total;
    }

    public Integer getProductId() {
        return productId;
    }

    public BigInteger getTotal() {
        return total;
    }
}

