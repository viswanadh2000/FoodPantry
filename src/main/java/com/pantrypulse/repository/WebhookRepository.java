package com.pantrypulse.repository;

import com.pantrypulse.model.Webhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WebhookRepository extends JpaRepository<Webhook, Long> {
    
    List<Webhook> findByActiveTrue();
    
    @Query("SELECT w FROM Webhook w WHERE w.active = true AND :event MEMBER OF w.events")
    List<Webhook> findActiveByEvent(String event);
}
