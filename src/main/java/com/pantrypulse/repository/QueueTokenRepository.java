package com.pantrypulse.repository;

import com.pantrypulse.model.QueueToken;
import com.pantrypulse.model.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QueueTokenRepository extends JpaRepository<QueueToken, Long> {
    
    Optional<QueueToken> findByTokenNumber(String tokenNumber);
    
    List<QueueToken> findBySiteAndStatusOrderByCreatedAtAsc(
        Site site, 
        QueueToken.TokenStatus status
    );
    
    @Query("SELECT COUNT(q) FROM QueueToken q WHERE q.site = :site AND q.status = 'WAITING'")
    long countWaitingBySite(Site site);
    
    @Query("SELECT q FROM QueueToken q WHERE q.site.id = :siteId ORDER BY q.createdAt DESC")
    List<QueueToken> findBySiteIdOrderByCreatedAtDesc(Long siteId);
}
