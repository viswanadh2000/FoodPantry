package com.pantrypulse.repository;

import com.pantrypulse.model.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    Page<AuditLog> findByUsernameOrderByTimestampDesc(String username, Pageable pageable);
    
    Page<AuditLog> findByEntityOrderByTimestampDesc(String entity, Pageable pageable);
    
    List<AuditLog> findByTimestampBetweenOrderByTimestampDesc(
        LocalDateTime start, 
        LocalDateTime end
    );
}
