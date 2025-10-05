package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.AuthorDto;
import com.bookstore.management.book.model.Author;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    Author toEntity(AuthorDto authorDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(AuthorDto authorDto,@MappingTarget Author author);
}
