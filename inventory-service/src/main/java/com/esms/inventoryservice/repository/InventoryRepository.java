package com.esms.inventoryservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.esms.inventoryservice.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    // Optional<Inventory> findBySkuCode();

    List<Inventory> findBySkuCodeIn(List<String> skuCode);
}
