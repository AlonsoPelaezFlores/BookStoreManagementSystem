package com.bookstore.management.sales.model;

import com.bookstore.management.book.model.Book;
import com.bookstore.management.customer.model.Customer;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sales_detail")
public class SalesDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_id", nullable = false)
    private Sale sale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id",nullable = false)
    private Book book;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    private void calculateLineTotal() {
        this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
