package com.shopmate.shippingservice.repositories;

import com.shopmate.shippingservice.models.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<Ship,String> {
}
