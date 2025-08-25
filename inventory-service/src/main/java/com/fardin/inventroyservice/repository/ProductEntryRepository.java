package com.fardin.inventroyservice.repository;

import com.fardin.inventroyservice.models.ProductEntry;
import com.fardin.inventroyservice.services.InventoryTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(path = "entries", collectionResourceRel = "entries")
public interface ProductEntryRepository extends JpaRepository<ProductEntry, String> {

    @Query("SELECT new com.fardin.inventroyservice.services.InventoryTotal(p.productId, SUM(p.quantity)) " +
            "FROM ProductEntry p " +
            "WHERE p.productId IN :productIds " +
            "GROUP BY p.productId")
    List<InventoryTotal> calculateInventoryByProductIds(@Param("productIds") List<Integer> productIds);


}

