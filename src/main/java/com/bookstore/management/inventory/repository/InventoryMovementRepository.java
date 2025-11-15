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

import java.time.LocalDate;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    @Query("SELECT im FROM InventoryMovement as im WHERE im.inventory.id = :inventoryId")
    Page<InventoryMovement> findAllByInventoryId(@Param("inventoryId")Long inventoryId, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    Page<InventoryMovement> findByCreatedAtBetween(@Param("startDate") LocalDate startDate,
                                                   @Param("endDate") LocalDate endDate,
                                                   Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    Page<InventoryMovement> findByMovementType(@Param("movementType") MovementType movementType, Pageable pageable);

    @EntityGraph(attributePaths = {"inventory", "inventory.book"})
    Page<InventoryMovement> findAllBy(Pageable pageable);
}
