package com.fardin.inventroyservice.repository;


import com.fardin.inventroyservice.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "transactions", collectionResourceRel = "transactions")
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    public Optional<Transaction> findByCheckoutId(String checkoutId);
    List<Transaction> findAllByCheckoutId(String checkoutId);
}
