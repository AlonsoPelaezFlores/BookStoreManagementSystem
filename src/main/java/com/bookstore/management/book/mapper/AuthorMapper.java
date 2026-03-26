package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.AuthorResponseDTO;
import com.bookstore.management.book.dto.AuthorSummaryDTO;
import com.bookstore.management.book.dto.CreateAuthorDTO;
import com.bookstore.management.book.model.Author;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    @Mapping(target = "id", ignore = true)
    Author toEntity(CreateAuthorDTO createAuthorDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(CreateAuthorDTO createAuthorDto, @MappingTarget Author author);

    AuthorResponseDTO toResponseDTO(Author author);
    List<AuthorResponseDTO> toResponseDTOList(List<Author> authors);

    AuthorSummaryDTO toSummaryDto(Author author);
    List<AuthorSummaryDTO> toSummaryDTOList(List<Author> authors);
}
