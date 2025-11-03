package com.pantrypulse.controller;

import com.pantrypulse.model.InventoryItem;
import com.pantrypulse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService service;

    @GetMapping
    public List<InventoryItem> all(){ return service.all(); }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public InventoryItem create(@RequestBody InventoryItem i){ return service.save(i); }
    
    @PatchMapping("/{id}/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public InventoryItem adjust(@PathVariable Long id, @RequestParam Integer quantity) {
        return service.adjustQuantity(id, quantity);
    }
    
    @GetMapping("/low-stock")
    public List<InventoryItem> lowStock(@RequestParam(defaultValue = "10") Integer threshold) {
        return service.findLowStock(threshold);
    }
}
