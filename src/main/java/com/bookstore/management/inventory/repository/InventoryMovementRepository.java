package com.bookstore.management.inventory.repository;

import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    @Query("SELECT im FROM InventoryMovement as im WHERE im.inventory.id = :inventoryId ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findAllByInventoryId(@Param("inventoryId")Long inventoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    @Query("SELECT im FROM InventoryMovement as im WHERE im.createdAt BETWEEN :startDate AND :endDate ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByCreatedAtBetween(@Param("startDate") LocalDateTime startDate,
                                                   @Param("endDate") LocalDateTime endDate,
                                                   Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    @Query("SELECT im FROM InventoryMovement as im WHERE im.movementType = :movementType ORDER BY im.createdAt DESC")
    Page<InventoryMovement> findByMovementType(@Param("movementType") MovementType movementType, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    @Query("SELECT im FROM InventoryMovement as im ORDER BY im.createdAt DESC" )
    Page<InventoryMovement> findLastNMovements(Pageable pageable);
}
