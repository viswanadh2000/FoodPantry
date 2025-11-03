package com.pantrypulse.controller;

import com.pantrypulse.repository.InventoryRepository;
import com.pantrypulse.repository.QueueTokenRepository;
import com.pantrypulse.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/metrics")
@RequiredArgsConstructor
public class MetricsController {
    
    private final SiteRepository siteRepo;
    private final InventoryRepository inventoryRepo;
    private final QueueTokenRepository queueRepo;

    @GetMapping
    public Map<String, Object> getGlobalMetrics() {
        long totalSites = siteRepo.count();
        long lowStockItems = inventoryRepo.countLowStock();
        long totalQueueTokens = queueRepo.count();
        long waitingTokens = queueRepo.findAll().stream()
            .filter(t -> t.getStatus() == com.pantrypulse.model.QueueToken.TokenStatus.WAITING)
            .count();
        
        return Map.of(
            "totalSites", totalSites,
            "lowStockItems", lowStockItems,
            "totalInventoryItems", inventoryRepo.count(),
            "totalQueueTokens", totalQueueTokens,
            "waitingQueueTokens", waitingTokens,
            "avgQueueLength", waitingTokens > 0 ? (double) waitingTokens / totalSites : 0.0
        );
    }
    
    @GetMapping("/site/{siteId}")
    public Map<String, Object> getSiteMetrics(@PathVariable Long siteId) {
        var site = siteRepo.findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException("Site not found: " + siteId));
        
        long siteInventoryCount = inventoryRepo.findAll().stream()
            .filter(item -> item.getSite().getId().equals(siteId))
            .count();
        
        long siteLowStockCount = inventoryRepo.findAll().stream()
            .filter(item -> item.getSite().getId().equals(siteId) && item.getQty() < 10)
            .count();
        
        long siteQueueCount = queueRepo.findBySiteIdOrderByCreatedAtDesc(siteId).size();
        
        long siteWaitingCount = queueRepo.findBySiteIdOrderByCreatedAtDesc(siteId).stream()
            .filter(t -> t.getStatus() == com.pantrypulse.model.QueueToken.TokenStatus.WAITING)
            .count();
        
        return Map.of(
            "siteId", siteId,
            "siteName", site.getName(),
            "inventoryItemCount", siteInventoryCount,
            "lowStockItemCount", siteLowStockCount,
            "totalQueueTokens", siteQueueCount,
            "waitingQueueTokens", siteWaitingCount
        );
    }
    
    @GetMapping("/by-city")
    public Map<String, Map<String, Object>> getMetricsByCity() {
        Map<String, Map<String, Object>> cityMetrics = new HashMap<>();
        
        siteRepo.findAll().forEach(site -> {
            String city = site.getCity();
            cityMetrics.putIfAbsent(city, new HashMap<>());
            Map<String, Object> metrics = cityMetrics.get(city);
            
            metrics.put("siteCount", ((Number) metrics.getOrDefault("siteCount", 0)).intValue() + 1);
        });
        
        return cityMetrics;
    }
}
