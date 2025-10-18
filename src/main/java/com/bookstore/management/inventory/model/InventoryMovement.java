package com.bookstore.management.inventory.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
@Table(name = "inventory_movement")
public class InventoryMovement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false, updatable = false)
    private Inventory inventory;

    @Column(name = "affected_quantity", nullable = false, updatable = false)
    private Integer affectedQuantity;

    @Column(name = "quantity_before", nullable = false, updatable = false)
    private Integer quantityBefore;

    @Column(name = "quantity_after", nullable = false, updatable = false)
    private Integer quantityAfter;

    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", updatable = false)
    private MovementType movementType;

    @Column(columnDefinition = "TEXT", updatable = false)
    private String description;

    @Column(name = "create_by", nullable = false, length = 100, updatable = false)
    @Builder.Default
    private String createBy ="SYSTEM";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
