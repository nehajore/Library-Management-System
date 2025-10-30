package com.library.controller;

import com.library.dto.BookDto;
import com.library.dto.LoanDto;
import com.library.entity.User;
import com.library.service.BookService;
import com.library.service.LoanService;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private BookService bookService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private UserService userService;

    // Book endpoints
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @GetMapping("/books/search")
    public ResponseEntity<List<BookDto>> searchBooks(@RequestParam String keyword) {
        return ResponseEntity.ok(bookService.searchBooks(keyword));
    }

    @GetMapping("/books/category/{category}")
    public ResponseEntity<List<BookDto>> getBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.getBooksByCategory(category));
    }

    @GetMapping("/books/available")
    public ResponseEntity<List<BookDto>> getAvailableBooks() {
        return ResponseEntity.ok(bookService.getAvailableBooks());
    }

    // Loan endpoints
    @GetMapping("/loans")
    public ResponseEntity<List<LoanDto>> getMyLoans(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(loanService.getLoansByUserId(user.getId()));
    }

    @GetMapping("/loans/active")
    public ResponseEntity<List<LoanDto>> getMyActiveLoans(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(loanService.getActiveLoansByUserId(user.getId()));
    }

    @PostMapping("/loans")
    public ResponseEntity<LoanDto> createLoan(
            @RequestParam Long bookId,
            @RequestParam(required = false, defaultValue = "14") Integer days,
            Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(loanService.createLoan(user.getId(), bookId, days));
    }

    @PutMapping("/loans/{id}/return")
    public ResponseEntity<LoanDto> returnLoan(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnLoan(id));
    }

    // User profile endpoints
    @GetMapping("/profile")
    public ResponseEntity<User> getMyProfile(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateMyProfile(@RequestBody User userDetails, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return ResponseEntity.ok(userService.updateUser(user.getId(), userDetails));
    }
}

