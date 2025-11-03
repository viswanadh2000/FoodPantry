package com.pantrypulse.controller;

import com.pantrypulse.model.ApiResponse;
import com.pantrypulse.model.AuditLog;
import com.pantrypulse.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/audit")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AuditService auditService;
    
    @GetMapping
    public ApiResponse<Page<AuditLog>> getAllAuditLogs(Pageable pageable) {
        return ApiResponse.success(auditService.getAllLogs(pageable), "Audit logs retrieved");
    }
    
    @GetMapping("/user/{username}")
    public ApiResponse<Page<AuditLog>> getAuditLogsByUser(
            @PathVariable String username, 
            Pageable pageable) {
        return ApiResponse.success(
            auditService.getLogsByUser(username, pageable),
            "User audit logs retrieved"
        );
    }
    
    @GetMapping("/entity/{entity}")
    public ApiResponse<Page<AuditLog>> getAuditLogsByEntity(
            @PathVariable String entity,
            Pageable pageable) {
        return ApiResponse.success(
            auditService.getLogsByEntity(entity, pageable),
            "Entity audit logs retrieved"
        );
    }
}
