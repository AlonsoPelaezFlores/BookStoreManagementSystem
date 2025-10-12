package com.bookstore.management.inventory.repository;

import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {

    List<InventoryMovement> findByIdOrderByCreatedAtDesc(Long bookId);

    List<InventoryMovement> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    List<InventoryMovement> findByMovementType(MovementType movementType);

    @Query(value = "SELECT im FROM inventory_movement as im ORDER BY im.created_at DESC LIMIT :n", nativeQuery = true )
    List<InventoryMovement> findLastNInventoryMovement(Integer n);
}
