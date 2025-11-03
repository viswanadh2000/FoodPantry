package com.pantrypulse.controller;

import com.pantrypulse.model.ApiResponse;
import com.pantrypulse.model.Webhook;
import com.pantrypulse.service.WebhookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
public class WebhookController {
    
    private final WebhookService webhookService;
    
    @PostMapping
    public ApiResponse<Webhook> registerWebhook(@Valid @RequestBody WebhookRequest req) {
        Webhook webhook = webhookService.registerWebhook(
            req.getUrl(),
            req.getEvents(),
            req.getDescription()
        );
        return ApiResponse.success(webhook, "Webhook registered successfully");
    }
    
    @GetMapping
    public ApiResponse<List<Webhook>> getAllWebhooks() {
        return ApiResponse.success(
            webhookService.getAllWebhooks(),
            "Webhooks retrieved successfully"
        );
    }
    
    @PatchMapping("/{id}")
    public ApiResponse<Webhook> updateWebhook(
            @PathVariable Long id,
            @RequestParam Boolean active) {
        return ApiResponse.success(
            webhookService.updateWebhook(id, active),
            "Webhook updated successfully"
        );
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteWebhook(@PathVariable Long id) {
        webhookService.deleteWebhook(id);
        return ApiResponse.success(null, "Webhook deleted successfully");
    }
    
    @Data
    public static class WebhookRequest {
        @NotBlank(message = "URL is required")
        private String url;
        
        @NotEmpty(message = "At least one event is required")
        private List<String> events;
        
        private String description;
    }
}
