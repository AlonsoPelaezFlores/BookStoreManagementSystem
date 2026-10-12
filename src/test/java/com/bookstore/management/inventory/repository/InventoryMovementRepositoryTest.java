package com.bookstore.management.inventory.repository;

import com.bookstore.management.book.model.Author;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.book.model.Gender;
import com.bookstore.management.inventory.model.Inventory;
import com.bookstore.management.inventory.model.InventoryMovement;
import com.bookstore.management.inventory.model.MovementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class InventoryMovementRepositoryTest {

    @Autowired
    private InventoryMovementRepository inventoryMovementRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("Find movements by createdAt range")
    class FindByCreatedAtRange {

        private static final LocalDate START = LocalDate.of(2024, 3, 10);
        private static final LocalDate END = LocalDate.of(2024, 3, 12);

        private Long lastInstantBeforeStart;
        private Long firstInstantOfStart;
        private Long lastInstantOfEnd;
        private Long firstInstantAfterEnd;

        @BeforeEach
        void setUp() {
            Author author = Author.builder()
                    .name("John Doe")
                    .nationality("Americano")
                    .birthDate(LocalDate.of(1980, 5, 15))
                    .gender(Gender.MALE)
                    .biography("Famoso Novelista")
                    .build();
            Book book = Book.builder()
                    .isbn("978-0-06-088328-7")
                    .title("Orgullo y prejuicio")
                    .publishDate(LocalDate.of(1813, 1, 28))
                    .description("Novela romántica")
                    .pages(432)
                    .genre("Romance")
                    .price(new BigDecimal("19.99"))
                    .author(author)
                    .build();
            Inventory inventory = Inventory.builder()
                    .book(book)
                    .quantityAvailable(10)
                    .build();
            entityManager.persist(author);
            entityManager.persist(book);
            entityManager.persist(inventory);

            lastInstantBeforeStart = persistMovementAt(inventory, START.minusDays(1).atTime(23, 59, 59));
            firstInstantOfStart = persistMovementAt(inventory, START.atStartOfDay());
            lastInstantOfEnd = persistMovementAt(inventory, END.atTime(23, 59, 59, 999_999_000));
            firstInstantAfterEnd = persistMovementAt(inventory, END.plusDays(1).atStartOfDay());

            entityManager.flush();
            entityManager.clear();
        }

        /**
         * createdAt is filled by @CreationTimestamp on insert, so the timestamp under test
         * is written afterwards with a native update.
         */
        private Long persistMovementAt(Inventory inventory, LocalDateTime createdAt) {
            InventoryMovement movement = InventoryMovement.builder()
                    .inventory(inventory)
                    .affectedQuantity(1)
                    .quantityBefore(10)
                    .quantityAfter(11)
                    .movementType(MovementType.ENTRY)
                    .build();
            entityManager.persistAndFlush(movement);
            entityManager.getEntityManager()
                    .createNativeQuery("UPDATE inventory_movement SET created_at = :createdAt WHERE id = :id")
                    .setParameter("createdAt", createdAt)
                    .setParameter("id", movement.getId())
                    .executeUpdate();
            return movement.getId();
        }

        @Test
        @DisplayName("Should include the whole start and end days and nothing outside them")
        void shouldIncludeWholeDaysOfTheRange() {
            Page<InventoryMovement> result = inventoryMovementRepository
                    .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                            START.atStartOfDay(), END.plusDays(1).atStartOfDay(), PageRequest.of(0, 10));

            assertThat(result.getContent())
                    .extracting(InventoryMovement::getId)
                    .containsExactlyInAnyOrder(firstInstantOfStart, lastInstantOfEnd)
                    .doesNotContain(lastInstantBeforeStart, firstInstantAfterEnd);
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("Should return only that day's movements when start and end are the same day")
        void shouldReturnSingleDayWhenStartEqualsEnd() {
            Page<InventoryMovement> result = inventoryMovementRepository
                    .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                            START.atStartOfDay(), START.plusDays(1).atStartOfDay(), PageRequest.of(0, 10));

            assertThat(result.getContent())
                    .extracting(InventoryMovement::getId)
                    .containsExactly(firstInstantOfStart);
        }

        @Test
        @DisplayName("Should paginate the movements in the range")
        void shouldPaginateMovementsInRange() {
            Page<InventoryMovement> firstPage = inventoryMovementRepository
                    .findByCreatedAtGreaterThanEqualAndCreatedAtLessThan(
                            START.atStartOfDay(), END.plusDays(1).atStartOfDay(), PageRequest.of(0, 1));

            assertThat(firstPage.getContent()).hasSize(1);
            assertThat(firstPage.getTotalElements()).isEqualTo(2);
            assertThat(firstPage.getTotalPages()).isEqualTo(2);
        }
    }
}
