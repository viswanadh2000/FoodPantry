package com.pantrypulse.service;

import com.pantrypulse.model.AuditLog;
import com.pantrypulse.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditService {
    
    private final AuditLogRepository repository;
    
    public void log(String action, String entity, Long entityId, String details) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = (auth != null && auth.isAuthenticated()) 
                ? auth.getName() 
                : "anonymous";
            
            AuditLog auditLog = AuditLog.builder()
                .username(username)
                .action(action)
                .entity(entity)
                .entityId(entityId)
                .details(details)
                .build();
                
            repository.save(auditLog);
            log.info("Audit: user={} action={} entity={} entityId={}", username, action, entity, entityId);
        } catch (Exception e) {
            log.error("Failed to create audit log", e);
        }
    }
    
    public Page<AuditLog> getAllLogs(Pageable pageable) {
        return repository.findAll(pageable);
    }
    
    public Page<AuditLog> getLogsByUser(String username, Pageable pageable) {
        return repository.findByUsernameOrderByTimestampDesc(username, pageable);
    }
    
    public Page<AuditLog> getLogsByEntity(String entity, Pageable pageable) {
        return repository.findByEntityOrderByTimestampDesc(entity, pageable);
    }
}
