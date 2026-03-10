package com.bookstore.management.sales.repository;

import com.bookstore.management.sales.model.Sale;
import com.bookstore.management.sales.model.SalesStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale,Long> {

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByCustomerId(Long customerId);

    @Query("SELECT s FROM Sale s WHERE DATE(s.createdAt) BETWEEN :start AND :end")
    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByDateRange(@Param("start") LocalDate start,@Param("end") LocalDate end);

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByStatus(SalesStatus status);

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByCustomerIdAndStatus(Long customerId, SalesStatus status);

    List<Sale> findByStatusAndExpiredAtBefore(SalesStatus status, LocalDateTime now);
}
