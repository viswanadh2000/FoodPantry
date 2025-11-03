package com.pantrypulse.repository;
import com.pantrypulse.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InventoryRepository extends JpaRepository<InventoryItem, Long> {
    
    @Query("SELECT COUNT(i) FROM InventoryItem i WHERE i.qty < 10")
    long countLowStock();
}

