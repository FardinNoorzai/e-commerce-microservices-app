package com.shopmate.cartservice.repositories;

import com.shopmate.cartservice.models.Checkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutRepository extends JpaRepository<Checkout, String> {

    public List<Checkout> findAllByUserId(String userId);
}
