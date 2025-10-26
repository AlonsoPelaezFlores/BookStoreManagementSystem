package com.bookstore.management.inventory.repository;

import com.bookstore.management.inventory.model.Inventory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Query("select i from Inventory i JOIN FETCH i.book WHERE i.book.id = :bookId")
    Optional<Inventory> findByBookId(@Param("bookId") Long bookId);

    @Query("select i from Inventory i JOIN FETCH i.book where i.stockMin > i.quantityAvailable AND i.activeStatus = true")
    List<Inventory> findActiveInventoriesWithLowStock();

    @Query("select i from Inventory i JOIN FETCH i.book where i.activeStatus = :activeStatus")
    List<Inventory> findByActiveStatus(@Param("activeStatus") Boolean activeStatus);
}
