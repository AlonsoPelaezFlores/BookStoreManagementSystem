package com.bookstore.management.inventory.mapper;

import com.bookstore.management.inventory.dto.InventoryMovementResponseDTO;
import com.bookstore.management.inventory.model.InventoryMovement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {InventoryMapper.class})
public interface InventoryMovementMapper {

    InventoryMovementResponseDTO toInventoryMovementResponseDTO(InventoryMovement inventoryMovement);

    List<InventoryMovementResponseDTO> toInventoryMovementResponseDTOList(List<InventoryMovement> inventoryMovements);

}
