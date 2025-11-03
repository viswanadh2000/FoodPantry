package com.pantrypulse.controller;

import com.pantrypulse.dto.SiteRequest;
import com.pantrypulse.model.ApiResponse;
import com.pantrypulse.model.Site;
import com.pantrypulse.service.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sites")
@RequiredArgsConstructor
public class SiteController {
    private final SiteService service;

    @GetMapping
    public ApiResponse<List<Site>> all(){ 
        return ApiResponse.success(service.getAllSites(), "Fetched successfully");
    }
    
    @GetMapping("/search")
    public ApiResponse<Page<Site>> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String state,
            Pageable pageable) {
        return ApiResponse.success(
            service.searchSites(city, state, pageable), 
            "Sites fetched successfully"
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Site>> one(@PathVariable Long id){
        return service.getSiteById(id)
            .map(site -> ResponseEntity.ok(ApiResponse.success(site)))
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ApiResponse<Site> create(@Valid @RequestBody SiteRequest req){ 
        Site site = new Site(null, req.name(), req.address(), req.city(), req.state(), req.zip());
        return ApiResponse.success(service.saveSite(site), "Site created successfully");
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<Site>> update(@PathVariable Long id, @Valid @RequestBody SiteRequest req) {
        return service.getSiteById(id)
            .map(existing -> {
                Site updated = new Site(id, req.name(), req.address(), req.city(), req.state(), req.zip());
                return ResponseEntity.ok(ApiResponse.success(service.saveSite(updated), "Site updated"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.getSiteById(id).ifPresent(site -> service.deleteSite(id));
        return ResponseEntity.ok(ApiResponse.success(null, "Site deleted"));
    }
}

