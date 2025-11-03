package com.pantrypulse.service;

import com.pantrypulse.model.Site;
import com.pantrypulse.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteService {
    private final SiteRepository repo;
    private final AuditService auditService;
    private final EventService eventService;

    @Cacheable(value = "sites", key = "'all'")
    public List<Site> getAllSites() { return repo.findAll(); }
    
    @Cacheable(value = "sites", key = "#city + '-' + #state + '-' + #pageable.pageNumber")
    public Page<Site> searchSites(String city, String state, Pageable pageable) {
        return repo.searchSites(city, state, pageable);
    }
    
    @Cacheable(value = "sites", key = "#id")
    public Optional<Site> getSiteById(Long id) { return repo.findById(id); }
    
    @CacheEvict(value = "sites", allEntries = true)
    public Site saveSite(Site s) { 
        Site saved = repo.save(s);
        String action = (s.getId() == null) ? "CREATE" : "UPDATE";
        auditService.log(action, "Site", saved.getId(), "Site: " + saved.getName());
        
        if (s.getId() == null) {
            eventService.publishEvent("site.created", "Site", saved.getId(),
                java.util.Map.of("name", saved.getName(), "city", saved.getCity()));
        } else {
            eventService.publishEvent("site.updated", "Site", saved.getId(),
                java.util.Map.of("name", saved.getName(), "city", saved.getCity()));
        }
        
        return saved;
    }
    
    @CacheEvict(value = "sites", allEntries = true)
    public void deleteSite(Long id) { 
        repo.deleteById(id);
        auditService.log("DELETE", "Site", id, "Site deleted");
        eventService.publishEvent("site.closed", "Site", id, java.util.Map.of("action", "deleted"));
    }
}

