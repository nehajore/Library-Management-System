package com.library.service;

import com.library.dto.BookDto;
import com.library.entity.Author;
import com.library.entity.Book;
import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.AuthorRepository;
import com.library.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    public List<BookDto> getAllBooks() {
        return bookRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        return convertToDto(book);
    }

    public List<BookDto> searchBooks(String keyword) {
        return bookRepository.searchBooks(keyword).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<BookDto> getBooksByCategory(String category) {
        return bookRepository.findByCategory(category).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<BookDto> getAvailableBooks() {
        return bookRepository.findByAvailableCopiesGreaterThan(0).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public BookDto createBook(BookDto bookDto) {
        if (bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new BadRequestException("Book with ISBN " + bookDto.getIsbn() + " already exists");
        }

        Author author = authorRepository.findById(bookDto.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthorId()));

        Book book = new Book();
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setAuthor(author);
        book.setPublisher(bookDto.getPublisher());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(bookDto.getTotalCopies());
        book.setDescription(bookDto.getDescription());
        book.setCategory(bookDto.getCategory());
        book.setLanguage(bookDto.getLanguage());
        book.setPages(bookDto.getPages());

        Book savedBook = bookRepository.save(book);
        return convertToDto(savedBook);
    }

    @Transactional
    public BookDto updateBook(Long id, BookDto bookDto) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));

        if (!book.getIsbn().equals(bookDto.getIsbn()) && 
            bookRepository.findByIsbn(bookDto.getIsbn()).isPresent()) {
            throw new BadRequestException("Book with ISBN " + bookDto.getIsbn() + " already exists");
        }

        Author author = authorRepository.findById(bookDto.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Author not found with id: " + bookDto.getAuthorId()));

        int copiesDifference = bookDto.getTotalCopies() - book.getTotalCopies();
        
        book.setTitle(bookDto.getTitle());
        book.setIsbn(bookDto.getIsbn());
        book.setAuthor(author);
        book.setPublisher(bookDto.getPublisher());
        book.setPublicationYear(bookDto.getPublicationYear());
        book.setTotalCopies(bookDto.getTotalCopies());
        book.setAvailableCopies(book.getAvailableCopies() + copiesDifference);
        book.setDescription(bookDto.getDescription());
        book.setCategory(bookDto.getCategory());
        book.setLanguage(bookDto.getLanguage());
        book.setPages(bookDto.getPages());

        Book updatedBook = bookRepository.save(book);
        return convertToDto(updatedBook);
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + id));
        
        if (!book.getLoans().isEmpty()) {
            throw new BadRequestException("Cannot delete book with active loans");
        }
        
        bookRepository.delete(book);
    }

    private BookDto convertToDto(Book book) {
        BookDto dto = new BookDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setAuthorName(book.getAuthor().getFullName());
        dto.setPublisher(book.getPublisher());
        dto.setPublicationYear(book.getPublicationYear());
        dto.setTotalCopies(book.getTotalCopies());
        dto.setAvailableCopies(book.getAvailableCopies());
        dto.setDescription(book.getDescription());
        dto.setCategory(book.getCategory());
        dto.setLanguage(book.getLanguage());
        dto.setPages(book.getPages());
        return dto;
    }
}

