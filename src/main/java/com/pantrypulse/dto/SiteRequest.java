package com.pantrypulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SiteRequest(
    @NotBlank(message = "Name is required")
    String name,
    
    @NotBlank(message = "Address is required")
    String address,
    
    @NotBlank(message = "City is required")
    String city,
    
    @NotBlank(message = "State is required")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State must be 2 uppercase letters")
    String state,
    
    @Pattern(regexp = "^\\d{5}(-\\d{4})?$", message = "Invalid ZIP code format")
    String zip
) {}
