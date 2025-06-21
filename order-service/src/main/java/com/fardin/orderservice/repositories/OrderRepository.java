package com.fardin.orderservice.repositories;

import com.fardin.orderservice.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "orders",collectionResourceRel = "orders")
public interface OrderRepository extends JpaRepository<Order, String> {
}
