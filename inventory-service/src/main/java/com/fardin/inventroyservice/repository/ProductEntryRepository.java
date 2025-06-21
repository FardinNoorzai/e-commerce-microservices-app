package com.fardin.inventroyservice.repository;

import com.fardin.inventroyservice.models.ProductEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "entries",collectionResourceRel = "entries")
public interface ProductEntryRepository extends JpaRepository<ProductEntry, String> {

    @Query("SELECT sum(p.quantity) FROM ProductEntry p WHERE p.productId = :productId")
    public Optional<BigInteger> calculateInventoryByProductId(@Param("productId") Integer productId);

}
