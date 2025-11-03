package com.pantrypulse.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "queue_token")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class QueueToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "site_id")
    private Site site;
    
    @Column(unique = true, nullable = false)
    private String tokenNumber;
    
    @Enumerated(EnumType.STRING)
    private TokenStatus status;
    
    private String contactName;
    
    private String contactPhone;
    
    private Integer estimatedWaitMinutes;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime calledAt;
    
    private LocalDateTime completedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = TokenStatus.WAITING;
        }
    }
    
    public enum TokenStatus {
        WAITING,
        CALLED,
        SERVING,
        COMPLETED,
        CANCELLED,
        NO_SHOW
    }
}
