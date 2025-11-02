package com.bookstore.management.book.repository;

import com.bookstore.management.book.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByNameContainingIgnoreCase(@Param("name") String name);

    List<Author> findByNationality(@Param("nationality") String nationality);

    List<Author> findByGender(@Param("gender") Author.Gender gender);
}
