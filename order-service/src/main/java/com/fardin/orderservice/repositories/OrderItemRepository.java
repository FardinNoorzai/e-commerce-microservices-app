package com.fardin.orderservice.repositories;

import com.fardin.orderservice.models.Order;
import com.fardin.orderservice.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, String> {
}
