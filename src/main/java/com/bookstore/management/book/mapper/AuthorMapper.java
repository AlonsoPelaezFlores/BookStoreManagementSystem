package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.model.Author;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    Author toEntity(CreateAuthorDTO createAuthorDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(CreateAuthorDTO createAuthorDto, @MappingTarget Author author);
}
