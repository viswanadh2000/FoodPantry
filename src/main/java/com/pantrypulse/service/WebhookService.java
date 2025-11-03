package com.pantrypulse.service;

import com.pantrypulse.model.Webhook;
import com.pantrypulse.repository.WebhookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebhookService {
    
    private final WebhookRepository webhookRepo;
    private final RestTemplate restTemplate = new RestTemplate();
    
    public Webhook registerWebhook(String url, List<String> events, String description) {
        Webhook webhook = Webhook.builder()
            .url(url)
            .events(new java.util.HashSet<>(events))
            .description(description)
            .active(true)
            .build();
        
        return webhookRepo.save(webhook);
    }
    
    public List<Webhook> getAllWebhooks() {
        return webhookRepo.findAll();
    }
    
    public Webhook updateWebhook(Long id, Boolean active) {
        Webhook webhook = webhookRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Webhook not found: " + id));
        webhook.setActive(active);
        return webhookRepo.save(webhook);
    }
    
    public void deleteWebhook(Long id) {
        webhookRepo.deleteById(id);
    }
    
    @Async
    public void triggerEvent(String eventType, Map<String, Object> payload) {
        List<Webhook> hooks = webhookRepo.findActiveByEvent(eventType);
        
        for (Webhook hook : hooks) {
            try {
                sendWebhook(hook, eventType, payload);
                hook.setLastTriggeredAt(LocalDateTime.now());
                webhookRepo.save(hook);
            } catch (Exception e) {
                log.error("Failed to send webhook to {}: {}", hook.getUrl(), e.getMessage());
            }
        }
    }
    
    private void sendWebhook(Webhook hook, String eventType, Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> body = Map.of(
            "event", eventType,
            "timestamp", LocalDateTime.now().toString(),
            "data", payload
        );
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(hook.getUrl(), request, String.class);
        
        log.info("Webhook sent: event={} url={}", eventType, hook.getUrl());
    }
}
