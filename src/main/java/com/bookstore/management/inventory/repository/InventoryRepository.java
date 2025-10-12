package com.bookstore.management.inventory.repository;

import com.bookstore.management.inventory.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByBookId(Long bookId);

    @Query("select i from Inventory i where i.stockMin > i.quantityAvailable AND i.activeState = true ")
    List<Inventory> findActiveInventoriesWithLowStock();

    List<Inventory>  findByActiveStateTrue();

    Optional<Inventory> findByBookIsbn(String isbn);

}
