package com.pantrypulse.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "webhook")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Webhook {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String url;
    
    @Column(nullable = false)
    private Boolean active;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "webhook_events", joinColumns = @JoinColumn(name = "webhook_id"))
    @Column(name = "event_type")
    private Set<String> events = new HashSet<>();
    
    private String description;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime lastTriggeredAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (active == null) {
            active = true;
        }
    }
}
