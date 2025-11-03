package com.pantrypulse.service;

import com.pantrypulse.model.QueueToken;
import com.pantrypulse.model.Site;
import com.pantrypulse.repository.QueueTokenRepository;
import com.pantrypulse.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueService {
    
    private final QueueTokenRepository queueRepo;
    private final SiteRepository siteRepo;
    private final AuditService auditService;
    private final EventService eventService;
    
    @Transactional
    public QueueToken createToken(Long siteId, String contactName, String contactPhone) {
        Site site = siteRepo.findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException("Site not found: " + siteId));
        
        String tokenNumber = generateTokenNumber(site);
        long waitingCount = queueRepo.countWaitingBySite(site);
        int estimatedWait = (int) (waitingCount * 15); // 15 min per person estimate
        
        QueueToken token = QueueToken.builder()
            .site(site)
            .tokenNumber(tokenNumber)
            .contactName(contactName)
            .contactPhone(contactPhone)
            .status(QueueToken.TokenStatus.WAITING)
            .estimatedWaitMinutes(estimatedWait)
            .build();
        
        QueueToken saved = queueRepo.save(token);
        auditService.log("CREATE", "QueueToken", saved.getId(), 
            "Token " + tokenNumber + " for " + contactName);
        
        eventService.publishEvent("queue.token.created", "QueueToken", saved.getId(),
            java.util.Map.of("tokenNumber", tokenNumber, "siteId", siteId, "estimatedWait", estimatedWait));
        
        return saved;
    }
    
    @Transactional
    public QueueToken updateTokenStatus(String tokenNumber, QueueToken.TokenStatus newStatus) {
        QueueToken token = queueRepo.findByTokenNumber(tokenNumber)
            .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenNumber));
        
        token.setStatus(newStatus);
        
        switch (newStatus) {
            case CALLED:
                token.setCalledAt(LocalDateTime.now());
                eventService.publishEvent("queue.token.called", "QueueToken", token.getId(),
                    java.util.Map.of("tokenNumber", tokenNumber, "siteId", token.getSite().getId()));
                break;
            case COMPLETED:
                token.setCompletedAt(LocalDateTime.now());
                eventService.publishEvent("queue.token.completed", "QueueToken", token.getId(),
                    java.util.Map.of("tokenNumber", tokenNumber, "siteId", token.getSite().getId()));
                break;
            case CANCELLED:
            case NO_SHOW:
                token.setCompletedAt(LocalDateTime.now());
                break;
            default:
                break;
        }
        
        QueueToken updated = queueRepo.save(token);
        auditService.log("UPDATE_STATUS", "QueueToken", updated.getId(), 
            "Status changed to " + newStatus);
        
        return updated;
    }
    
    public List<QueueToken> getWaitingTokens(Long siteId) {
        Site site = siteRepo.findById(siteId)
            .orElseThrow(() -> new IllegalArgumentException("Site not found: " + siteId));
        return queueRepo.findBySiteAndStatusOrderByCreatedAtAsc(site, QueueToken.TokenStatus.WAITING);
    }
    
    public QueueToken getTokenByNumber(String tokenNumber) {
        return queueRepo.findByTokenNumber(tokenNumber)
            .orElseThrow(() -> new IllegalArgumentException("Token not found: " + tokenNumber));
    }
    
    public List<QueueToken> getTokensBySite(Long siteId) {
        return queueRepo.findBySiteIdOrderByCreatedAtDesc(siteId);
    }
    
    private String generateTokenNumber(Site site) {
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = queueRepo.count() + 1;
        return String.format("%s-%s-%04d", site.getCity().substring(0, 3).toUpperCase(), date, count);
    }
}
