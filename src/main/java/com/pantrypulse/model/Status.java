package com.pantrypulse.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Status {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Site site;
    private String state; // OPEN|PAUSED|CLOSED
    private Integer queueLen;
    private Double serviceRatePph;
}
