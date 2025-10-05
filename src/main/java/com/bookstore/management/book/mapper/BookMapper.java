package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.BookDto;
import com.bookstore.management.book.model.Book;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book toEntity(BookDto bookDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(BookDto bookDto,@MappingTarget Book book);

}
