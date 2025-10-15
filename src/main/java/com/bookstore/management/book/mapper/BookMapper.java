package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.model.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book toEntity(CreateBookDTO createBookDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CreateBookDTO createBookDto, @MappingTarget Book book);

}
