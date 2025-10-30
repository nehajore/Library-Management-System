package com.library.service;

import com.library.dto.LoanDto;
import com.library.entity.Book;
import com.library.entity.Loan;
import com.library.entity.User;
import com.library.entity.Loan.LoanStatus;
import com.library.exception.BadRequestException;
import com.library.exception.ResourceNotFoundException;
import com.library.repository.BookRepository;
import com.library.repository.LoanRepository;
import com.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public List<LoanDto> getAllLoans() {
        return loanRepository.findAll().stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<LoanDto> getLoansByUserId(Long userId) {
        return loanRepository.findByUserId(userId).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<LoanDto> getActiveLoansByUserId(Long userId) {
        return loanRepository.findByUserIdAndStatus(userId, LoanStatus.ACTIVE).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public List<LoanDto> getOverdueLoans() {
        return loanRepository.findOverdueLoans(LocalDate.now()).stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
    }

    public LoanDto getLoanById(Long id) {
        Loan loan = loanRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + id));
        return convertToDto(loan);
    }

    @Transactional
    public LoanDto createLoan(Long userId, Long bookId, Integer days) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        if (book.getAvailableCopies() <= 0) {
            throw new BadRequestException("Book is not available");
        }

        if (loanRepository.findActiveLoanByUserAndBook(userId, bookId).isPresent()) {
            throw new BadRequestException("User already has an active loan for this book");
        }

        Long activeLoanCount = loanRepository.countByUserIdAndStatus(userId, LoanStatus.ACTIVE);
        if (activeLoanCount >= 5) {
            throw new BadRequestException("User has reached maximum loan limit (5 books)");
        }

        Loan loan = new Loan();
        loan.setUser(user);
        loan.setBook(book);
        loan.setLoanDate(LocalDate.now());
        loan.setLoanPeriodDays(days != null ? days : 14);
        loan.setDueDate(LocalDate.now().plusDays(loan.getLoanPeriodDays()));
        loan.setStatus(LoanStatus.ACTIVE);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        Loan savedLoan = loanRepository.save(loan);
        return convertToDto(savedLoan);
    }

    @Transactional
    public LoanDto returnLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() -> new ResourceNotFoundException("Loan not found with id: " + loanId));

        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new BadRequestException("Loan is not active");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        Loan updatedLoan = loanRepository.save(loan);
        return convertToDto(updatedLoan);
    }

    @Transactional
    public void updateOverdueLoans() {
        List<Loan> overdueLoans = loanRepository.findOverdueLoans(LocalDate.now());
        for (Loan loan : overdueLoans) {
            if (loan.getStatus() == LoanStatus.ACTIVE) {
                loan.setStatus(LoanStatus.OVERDUE);
                long daysOverdue = LocalDate.now().toEpochDay() - loan.getDueDate().toEpochDay();
                loan.setFineAmount(daysOverdue * 0.50); // $0.50 per day
                loanRepository.save(loan);
            }
        }
    }

    private LoanDto convertToDto(Loan loan) {
        LoanDto dto = new LoanDto();
        dto.setId(loan.getId());
        dto.setUserId(loan.getUser().getId());
        dto.setUserName(loan.getUser().getUsername());
        dto.setBookId(loan.getBook().getId());
        dto.setBookTitle(loan.getBook().getTitle());
        dto.setBookIsbn(loan.getBook().getIsbn());
        dto.setLoanDate(loan.getLoanDate());
        dto.setDueDate(loan.getDueDate());
        dto.setReturnDate(loan.getReturnDate());
        dto.setStatus(loan.getStatus().name());
        dto.setFineAmount(loan.getFineAmount());
        dto.setLoanPeriodDays(loan.getLoanPeriodDays());
        return dto;
    }
}

