package com.fardin.inventroyservice.repository;

import com.fardin.inventroyservice.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {

    public List<Item> findByProductId(Integer productId);
}
