package com.bookstore.management.book.repository;

import com.bookstore.management.book.model.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<Book> findBooksByAuthorId(@Param("authorId") Long authorId);

    Optional<Book> findBookByIsbn(@Param("isbn") String isbn);
    List<Book> findBookByTitleContainingIgnoreCase(@Param("title") String title);

    boolean existsBookByIsbn(@Param("isbn") String isbn);
}
