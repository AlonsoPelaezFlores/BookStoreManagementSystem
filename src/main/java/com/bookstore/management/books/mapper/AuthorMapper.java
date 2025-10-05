package com.bookstore.management.books.mapper;

import com.bookstore.management.books.dto.AuthorDto;
import com.bookstore.management.books.model.Author;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    Author toEntity(AuthorDto authorDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AuthorDto authorDto,@MappingTarget Author author);
}
