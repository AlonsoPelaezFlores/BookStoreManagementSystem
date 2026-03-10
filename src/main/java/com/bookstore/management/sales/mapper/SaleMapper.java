package com.bookstore.management.sales.mapper;

import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.customer.mapper.CustomerMapper;
import com.bookstore.management.sales.dto.SaleResponseDTO;
import com.bookstore.management.sales.model.Sale;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, BookMapper.class})
public interface SaleMapper {

    SaleResponseDTO toResponseDto(Sale  sale);
    List<SaleResponseDTO> toResponseDtoList(List<Sale> sales);
}
