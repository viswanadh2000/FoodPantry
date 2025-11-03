package com.pantrypulse.service;

import com.pantrypulse.model.InventoryItem;
import com.pantrypulse.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository repo;
    private final EventService eventService;
    
    public List<InventoryItem> all(){ return repo.findAll(); }
    
    public InventoryItem save(InventoryItem i){ 
        InventoryItem saved = repo.save(i);
        eventService.publishEvent("inventory.updated", "InventoryItem", saved.getId(),
            java.util.Map.of("sku", saved.getSku(), "qty", saved.getQty()));
        return saved;
    }
    
    public InventoryItem adjustQuantity(Long id, Integer adjustment) {
        InventoryItem item = repo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventory item not found: " + id));
        
        int oldQty = item.getQty();
        item.setQty(item.getQty() + adjustment);
        InventoryItem updated = repo.save(item);
        
        // Publish event if stock becomes low
        if (updated.getQty() < 10) {
            eventService.publishEvent("inventory.low", "InventoryItem", updated.getId(),
                java.util.Map.of("sku", updated.getSku(), "qty", updated.getQty(), "previousQty", oldQty));
        }
        
        eventService.publishEvent("inventory.updated", "InventoryItem", updated.getId(),
            java.util.Map.of("sku", updated.getSku(), "qty", updated.getQty(), "adjustment", adjustment));
        
        return updated;
    }
    
    public List<InventoryItem> findLowStock(Integer threshold) {
        return repo.findAll().stream()
            .filter(item -> item.getQty() < threshold)
            .toList();
    }
}
