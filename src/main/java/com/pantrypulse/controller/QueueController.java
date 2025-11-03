package com.pantrypulse.controller;

import com.pantrypulse.model.ApiResponse;
import com.pantrypulse.model.QueueToken;
import com.pantrypulse.service.QueueService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueController {
    
    private final QueueService queueService;
    
    @PostMapping("/tokens")
    public ApiResponse<QueueToken> createToken(@Valid @RequestBody CreateTokenRequest req) {
        QueueToken token = queueService.createToken(
            req.getSiteId(), 
            req.getContactName(), 
            req.getContactPhone()
        );
        return ApiResponse.success(token, "Queue token created successfully");
    }
    
    @GetMapping("/tokens/{tokenNumber}")
    public ApiResponse<QueueToken> getTokenStatus(@PathVariable String tokenNumber) {
        return ApiResponse.success(
            queueService.getTokenByNumber(tokenNumber),
            "Token status retrieved"
        );
    }
    
    @PatchMapping("/tokens/{tokenNumber}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ApiResponse<QueueToken> updateTokenStatus(
            @PathVariable String tokenNumber,
            @RequestParam QueueToken.TokenStatus status) {
        return ApiResponse.success(
            queueService.updateTokenStatus(tokenNumber, status),
            "Token status updated"
        );
    }
    
    @GetMapping("/sites/{siteId}/waiting")
    public ApiResponse<List<QueueToken>> getWaitingTokens(@PathVariable Long siteId) {
        return ApiResponse.success(
            queueService.getWaitingTokens(siteId),
            "Waiting tokens retrieved"
        );
    }
    
    @GetMapping("/sites/{siteId}/tokens")
    public ApiResponse<List<QueueToken>> getAllTokensForSite(@PathVariable Long siteId) {
        return ApiResponse.success(
            queueService.getTokensBySite(siteId),
            "Site tokens retrieved"
        );
    }
    
    @Data
    public static class CreateTokenRequest {
        @NotNull(message = "Site ID is required")
        private Long siteId;
        
        @NotBlank(message = "Contact name is required")
        private String contactName;
        
        private String contactPhone;
    }
}
