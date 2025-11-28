package com.bookstore.management.sales.mapper;

import com.bookstore.management.book.mapper.BookMapper;
import com.bookstore.management.sales.dto.SalesDetailResponseDTO;
import com.bookstore.management.sales.model.SalesDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {BookMapper.class})
public interface SalesDetailMapper {

    SalesDetailResponseDTO  toResponseDto(SalesDetail salesDetail);
}
