package com.bookstore.management.inventory.repository;

import com.bookstore.management.inventory.model.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from Inventory i JOIN FETCH i.book WHERE i.book.id = :bookId")
    Optional<Inventory> findByBookId(@Param("bookId") Long bookId);

    @Query(value = "select i from Inventory i JOIN FETCH i.book where i.stockMin > i.quantityAvailable AND i.activeStatus = true",
            countQuery = "select count(i) from Inventory i where i.stockMin > i.quantityAvailable AND i.activeStatus = true")
    Page<Inventory> findActiveInventoriesWithLowStock(Pageable pageable);

    @Query(value = "select i from Inventory i JOIN FETCH i.book where i.activeStatus = :activeStatus",
            countQuery = "select count(i) from Inventory i where i.activeStatus = :activeStatus")
    Page<Inventory> findByActiveStatus(@Param("activeStatus") Boolean activeStatus, Pageable pageable);
}
