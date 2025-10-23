package com.bookstore.management.inventory.mapper;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.inventory.dto.CreateInventoryDTO;
import com.bookstore.management.inventory.dto.InventoryResponseDTO;
import com.bookstore.management.inventory.dto.InventorySummaryDTO;
import com.bookstore.management.inventory.model.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    Inventory toEntity(CreateInventoryDTO createInventoryDto);

    @Mapping(target = "realStockAvailable", expression = "java(inventory.getQuantityAvailable() - inventory.getQuantityReserved())")
    @Mapping(target = "book",expression = "java(debugMapBook(inventory))")
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);

    @Mapping(target = "book", expression = "java(debugMapBook(inventory))")
    InventorySummaryDTO toInventorySummaryDTO(Inventory inventory);

    List<InventorySummaryDTO> toInventorySummaryDTOList(List<Inventory> inventories);

    default BookSummaryDTO debugMapBook(Inventory inventory) {
        if (inventory == null || inventory.getBook() == null){
            return null;
        }
        Book book = inventory.getBook();
        return new BookSummaryDTO(book.getId(), book.getTitle(), book.getIsbn(), book.getAuthor().getName());
    }
}
