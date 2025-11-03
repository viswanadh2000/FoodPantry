package com.pantrypulse.controller;

import com.pantrypulse.service.JwtService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        // For starter template: accept any username/password and issue JWT.
        // Replace with real user store later.
        if (req.getUsername() == null || req.getUsername().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username required"));
        }
        
        // Validate role
        String role = req.getRole();
        if (role == null || role.isBlank()) {
            role = "GUEST"; // default to GUEST if not provided
        }
        
        // Normalize role to uppercase and add ROLE_ prefix if needed
        role = role.trim().toUpperCase();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        
        // Validate against allowed roles
        if (!role.equals("ROLE_ADMIN") && !role.equals("ROLE_OPERATOR") && !role.equals("ROLE_GUEST")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Must be ADMIN, OPERATOR, or GUEST"));
        }
        
        // Issue JWT with selected role
        String token = jwtService.issue(req.getUsername(), Map.of("role", role));
        return ResponseEntity.ok(Map.of(
            "token", token, 
            "username", req.getUsername(),
            "role", role
        ));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid authorization header"));
        }
        
        String oldToken = authHeader.substring(7);
        try {
            // Parse old token to extract username and role
            var claims = jwtService.parse(oldToken);
            String username = claims.getSubject();
            String role = (String) claims.get("role");
            
            if (role == null || role.isBlank()) {
                role = "ROLE_GUEST";
            }
            
            // Issue new token with same username and role
            String newToken = jwtService.issue(username, Map.of("role", role));
            return ResponseEntity.ok(Map.of(
                "token", newToken, 
                "username", username,
                "role", role,
                "message", "Token refreshed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
        }
    }

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
        private String role; // ADMIN, OPERATOR, or GUEST
    }
}
