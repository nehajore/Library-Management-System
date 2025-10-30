package com.library.repository;

import com.library.entity.Loan;
import com.library.entity.Loan.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);
    List<Loan> findByBookId(Long bookId);
    List<Loan> findByStatus(LoanStatus status);
    List<Loan> findByUserIdAndStatus(Long userId, LoanStatus status);
    
    @Query("SELECT l FROM Loan l WHERE l.dueDate < :date AND l.status = 'ACTIVE'")
    List<Loan> findOverdueLoans(@Param("date") LocalDate date);
    
    @Query("SELECT l FROM Loan l WHERE l.user.id = :userId AND l.book.id = :bookId AND l.status = 'ACTIVE'")
    Optional<Loan> findActiveLoanByUserAndBook(@Param("userId") Long userId, @Param("bookId") Long bookId);
    
    Long countByUserIdAndStatus(Long userId, LoanStatus status);
}

