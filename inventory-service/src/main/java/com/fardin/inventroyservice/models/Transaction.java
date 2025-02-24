package com.fardin.inventroyservice.models;

import com.shopmate.states.InventoryStates;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    String username;
    String orderId;
    BigDecimal quantity;
    Integer productId;
    @Enumerated(EnumType.STRING)
    InventoryStates state;
    LocalDateTime timestamp;
}
