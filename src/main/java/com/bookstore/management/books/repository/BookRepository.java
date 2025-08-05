package com.bookstore.management.books.repository;

import com.bookstore.management.books.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBooksByAuthorId(Long id);

    Optional<Book> findBookByIsbn(String isbn);
    List<Book> findBookByTitleContainingIgnoreCase(String title);

}
