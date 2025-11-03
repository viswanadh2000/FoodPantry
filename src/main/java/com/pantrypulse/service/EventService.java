package com.pantrypulse.service;

import com.pantrypulse.model.SystemEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventService {
    
    private final Sinks.Many<SystemEvent> eventSink = Sinks.many().multicast().onBackpressureBuffer();
    private final WebhookService webhookService;
    
    public void publishEvent(String eventType, String entity, Long entityId, Map<String, Object> data) {
        SystemEvent event = SystemEvent.builder()
            .eventType(eventType)
            .entity(entity)
            .entityId(entityId)
            .data(data)
            .timestamp(LocalDateTime.now())
            .build();
        
        eventSink.tryEmitNext(event);
        log.info("Event published: type={} entity={} entityId={}", eventType, entity, entityId);
        
        // Trigger webhooks asynchronously
        webhookService.triggerEvent(eventType, Map.of(
            "entity", entity,
            "entityId", entityId,
            "data", data,
            "timestamp", event.getTimestamp().toString()
        ));
    }
    
    public Flux<SystemEvent> getEventStream() {
        return eventSink.asFlux();
    }
}
