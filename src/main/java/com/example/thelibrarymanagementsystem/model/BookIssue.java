package com.example.thelibrarymanagementsystem.model;

import java.time.LocalDate;

public class BookIssue extends BorrowRecord {
    public BookIssue(String issueId, String studentId, String studentName, String bookId, String bookTitle,
                     String adminId, String adminName, String depositId, LocalDate issueDate, LocalDate dueDate,
                     LocalDate returnDate, double fineAmount, boolean finePaid) {
        super(issueId, studentId, studentName, bookId, bookTitle, adminId, adminName, depositId,
                issueDate, dueDate, returnDate, fineAmount, finePaid);
    }
}
