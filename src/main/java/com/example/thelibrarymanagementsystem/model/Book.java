package com.example.thelibrarymanagementsystem.model;

public class Book {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private String publisher;
    private String category;
    private String language;
    private String shelfNo;
    private String rackNo;
    private int totalCopies;
    private int availableCopies;

    public Book(String bookId, String isbn, String title, String author, String publisher, String category,
                String language, String shelfNo, String rackNo, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
        this.language = language;
        this.shelfNo = shelfNo;
        this.rackNo = rackNo;
        this.totalCopies = Math.max(0, totalCopies);
        this.availableCopies = Math.max(0, Math.min(availableCopies, this.totalCopies));
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = bookId; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getBookTitle() { return title; }
    public void setBookTitle(String bookTitle) { this.title = bookTitle; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getShelfNo() { return shelfNo; }
    public void setShelfNo(String shelfNo) { this.shelfNo = shelfNo; }
    public String getRackNo() { return rackNo; }
    public void setRackNo(String rackNo) { this.rackNo = rackNo; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) { this.totalCopies = Math.max(0, totalCopies); }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = Math.max(0, Math.min(availableCopies, totalCopies));
    }
    public String getStatus() { return availableCopies > 0 ? "AVAILABLE" : "ISSUED"; }
    public String getAvailabilityStatus() { return getStatus(); }
}
