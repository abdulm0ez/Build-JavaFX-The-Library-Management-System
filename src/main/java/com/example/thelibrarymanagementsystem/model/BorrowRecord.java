package com.example.thelibrarymanagementsystem.model;

import java.time.LocalDate;

public class BorrowRecord {
    private String recordId;
    private String studentId;
    private String studentName;
    private String bookId;
    private String bookTitle;
    private String adminId;
    private String adminName;
    private String depositId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;
    private boolean finePaid;

    public BorrowRecord(String recordId, String studentId, String studentName, String bookId, String bookTitle,
                        LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, double fine, boolean finePaid) {
        this(recordId, studentId, studentName, bookId, bookTitle, "", "", "", issueDate, dueDate, returnDate, fine, finePaid);
    }

    public BorrowRecord(String recordId, String studentId, String studentName, String bookId, String bookTitle,
                        String adminId, String adminName, String depositId, LocalDate issueDate, LocalDate dueDate,
                        LocalDate returnDate, double fine, boolean finePaid) {
        this.recordId = recordId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.adminId = adminId;
        this.adminName = adminName;
        this.depositId = depositId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
        this.finePaid = finePaid;
    }

    public String getRecordId() { return recordId; }
    public String getIssueId() { return recordId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getAdminId() { return adminId; }
    public String getAdminName() { return adminName; }
    public String getDepositId() { return depositId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    public double getFine() { return fine; }
    public void setFine(double fine) { this.fine = fine; }
    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }
    public String getStatus() { return returnDate == null ? "BORROWED" : "RETURNED"; }
    public String getIssueStatus() { return getStatus(); }
    public String getFineStatus() { return fine <= 0 ? "NONE" : finePaid ? "PAID" : "UNPAID"; }
}
