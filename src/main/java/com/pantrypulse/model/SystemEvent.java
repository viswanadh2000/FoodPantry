package com.pantrypulse.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemEvent {
    
    private String eventType;
    private String entity;
    private Long entityId;
    private Map<String, Object> data;
    private LocalDateTime timestamp;
    
    public enum EventType {
        INVENTORY_LOW("inventory.low"),
        INVENTORY_UPDATED("inventory.updated"),
        SITE_CREATED("site.created"),
        SITE_UPDATED("site.updated"),
        SITE_CLOSED("site.closed"),
        QUEUE_TOKEN_CREATED("queue.token.created"),
        QUEUE_TOKEN_CALLED("queue.token.called"),
        QUEUE_TOKEN_COMPLETED("queue.token.completed");
        
        private final String value;
        
        EventType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
    }
}
