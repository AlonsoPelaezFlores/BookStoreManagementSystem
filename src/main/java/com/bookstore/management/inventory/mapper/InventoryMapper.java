package com.bookstore.management.inventory.mapper;

import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.book.model.Book;
import com.bookstore.management.inventory.dto.CreateInventoryDTO;
import com.bookstore.management.inventory.dto.InventoryResponseDTO;
import com.bookstore.management.inventory.dto.InventorySummaryDTO;
import com.bookstore.management.inventory.model.Inventory;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BookMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface InventoryMapper {

    @Mapping(target = "id", ignore = true)
    Inventory toEntity(CreateInventoryDTO createInventoryDto);

    @Mapping(target = "realStockAvailable", expression = "java(inventory.getQuantityAvailable() - inventory.getQuantityReserved())")
    @Mapping(target = "bookSummaryDTO",source = "book")
    InventoryResponseDTO toInventoryResponseDTO(Inventory inventory);

    @Mapping(target = "bookSummaryDTO",source = "book")
    InventorySummaryDTO toInventorySummaryDTO(Inventory inventory);

    List<InventorySummaryDTO> toInventorySummaryDTOList(List<Inventory> inventories);

}
