package com.ecommerce.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String aggregateId;
    private String aggregateType;
    private String eventType;
    
    @Column(columnDefinition = "TEXT")
    private String payload;
    
    private LocalDateTime createdAt;
    private boolean processed;
}
