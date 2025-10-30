package com.library.repository;

import com.library.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);
    List<Book> findByTitleContainingIgnoreCase(String title);
    List<Book> findByAuthorId(Long authorId);
    List<Book> findByCategory(String category);
    List<Book> findByAvailableCopiesGreaterThan(int count);
    
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.isbn LIKE %:keyword%")
    List<Book> searchBooks(@Param("keyword") String keyword);
}

