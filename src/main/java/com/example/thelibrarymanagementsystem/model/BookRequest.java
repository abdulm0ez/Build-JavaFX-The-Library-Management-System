package com.example.thelibrarymanagementsystem.model;

import java.time.LocalDate;

public class BookRequest {
    private final String requestId;
    private final String studentId;
    private final String studentName;
    private final String bookId;
    private final String bookTitle;
    private final String issueId;
    private final RequestType requestType;
    private final LocalDate requestDate;
    private RequestStatus requestStatus;

    public BookRequest(String requestId, String studentId, String studentName, String bookId, String bookTitle,
                       String issueId, RequestType requestType, LocalDate requestDate, RequestStatus requestStatus) {
        this.requestId = requestId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueId = issueId;
        this.requestType = requestType;
        this.requestDate = requestDate;
        this.requestStatus = requestStatus;
    }

    public String getRequestId() { return requestId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getIssueId() { return issueId; }
    public RequestType getRequestType() { return requestType; }
    public LocalDate getRequestDate() { return requestDate; }
    public RequestStatus getRequestStatus() { return requestStatus; }
    public void setRequestStatus(RequestStatus requestStatus) { this.requestStatus = requestStatus; }
}
