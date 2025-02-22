package com.fardin.inventroyservice.models;

import com.shopmate.states.InventoryStates;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.math.BigDecimal;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    String username;
    BigDecimal quantity;
    Integer productId;
}
