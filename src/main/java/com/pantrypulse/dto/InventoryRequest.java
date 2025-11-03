package com.pantrypulse.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record InventoryRequest(
    @NotNull(message = "Site ID is required")
    Long siteId,
    
    @NotBlank(message = "SKU is required")
    String sku,
    
    @NotBlank(message = "Name is required")
    String name,
    
    String tags,
    
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    Integer qty,
    
    @NotBlank(message = "Unit is required")
    String unit
) {}
