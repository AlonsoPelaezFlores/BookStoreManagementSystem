package com.bookstore.management.inventory.model;

import com.bookstore.management.book.model.Book;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Inventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", unique = true, nullable = false)
    private Book book;

    @Column(name = "quantity_available", nullable = false)
    @Builder.Default
    private Integer quantityAvailable = 0;

    @Column(name = "reserved_quantity", nullable = false)
    @Builder.Default
    private Integer quantityReserved = 0;

    @Column(name = "stock_min", nullable = false)
    @Builder.Default
    private Integer stockMin = 0;

    @Column(name = "stock_max", nullable = false)
    @Builder.Default
    private Integer stockMax = 9999;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @Column(name = "active_state", nullable = false)
    @Builder.Default
    private Boolean activeState = true ;

    @OneToMany(mappedBy = "inventory", cascade = CascadeType.ALL)
    private List<InventoryMovement> movements;
}