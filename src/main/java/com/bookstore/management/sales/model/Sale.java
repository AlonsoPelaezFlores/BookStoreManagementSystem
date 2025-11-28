package com.bookstore.management.sales.model;

import com.bookstore.management.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sale")
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SalesStatus status = SalesStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod paymentMethod =  PaymentMethod.CASH;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal discountPercentCustomer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany( mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalesDetail> details;

    private String observation;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Long createdBy;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private void addDetail(SalesDetail detail) {
        details.add(detail);
        detail.setSale(this);
    }
}
