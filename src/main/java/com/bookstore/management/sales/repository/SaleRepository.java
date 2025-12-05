package com.bookstore.management.sales.repository;

import com.bookstore.management.sales.model.Sale;
import com.bookstore.management.sales.model.SalesStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale,Long> {

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    Optional<Sale> findByCustomerId(Long customerId);

    @Query("SELECT s FROM Sale s WHERE DATE(s.createdAt) BETWEEN :start AND :end")
    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByDateRange(@Param("start") LocalDate start,@Param("end") LocalDate end);

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByStatus(SalesStatus status);

    @EntityGraph(attributePaths = {"customer","details","details.book"})
    List<Sale> findByCustomerIdAndStatus(Long customerId, SalesStatus status);
}
