package com.bookstore.management.books.mapper;

import com.bookstore.management.books.dto.BookDto;
import com.bookstore.management.books.model.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book toEntity(BookDto bookDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BookDto bookDto,@MappingTarget Book book);

}
