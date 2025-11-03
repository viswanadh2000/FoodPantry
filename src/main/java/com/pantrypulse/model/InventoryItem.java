package com.pantrypulse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class InventoryItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Site site;
    private String sku;
    private String name;
    private String tags; // comma-separated for starter
    private Integer qty;
    private String unit;
}
