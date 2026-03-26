package com.bookstore.management.book.mapper;

import com.bookstore.management.book.dto.BookResponseDTO;
import com.bookstore.management.book.dto.BookSummaryDTO;
import com.bookstore.management.book.dto.CreateBookDTO;
import com.bookstore.management.book.model.Book;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class})
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    Book toEntity(CreateBookDTO createBookDto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updateEntityFromDto(CreateBookDTO createBookDto, @MappingTarget Book book);

    @Mapping(target = "author", source = "book.author.name")
    BookSummaryDTO toBookSummaryDTO(Book book);
    List<BookSummaryDTO> toBookSummaryDTOList(List<Book> books);

    BookResponseDTO toBookResponseDTO(Book book);
}
