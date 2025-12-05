package com.bookstore.management.sales.repository;

import com.bookstore.management.sales.model.SalesDetail;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesDetailRepository extends JpaRepository<SalesDetail, Long> {

    @EntityGraph(attributePaths = {"book","sale"})
    List<SalesDetail> findByBookId(Long bookId);
}
