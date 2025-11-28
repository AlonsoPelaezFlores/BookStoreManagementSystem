package com.bookstore.management.sales.mapper;

import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.customer.mapper.CustomerMapper;
import com.bookstore.management.sales.dto.SalesResponseDTO;
import com.bookstore.management.sales.model.Sale;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {CustomerMapper.class, BookMapper.class})
public interface SalesMapper {

    SalesResponseDTO toResponseDto(Sale  sale);
}
