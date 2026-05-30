package com.example.thelibrarymanagementsystem.service;

import com.example.thelibrarymanagementsystem.model.*;
import com.example.thelibrarymanagementsystem.util.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

public class LibraryService {
    public static final double FINE_PER_DAY = 25.0;
    public static final int MAX_ACTIVE_LOANS_PER_STUDENT = 3;
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE = Pattern.compile("^[0-9+()\\-\\s]{7,20}$");

    private final FileHandler fileHandler = new FileHandler();
    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private final ObservableList<Student> students = FXCollections.observableArrayList();
    private final ObservableList<BorrowRecord> history = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ObservableList<ActivityLog> activityLogs = FXCollections.observableArrayList();
    private final ObservableList<SecurityDeposit> deposits = FXCollections.observableArrayList();
    private final ObservableList<BookRequest> requests = FXCollections.observableArrayList();

    public LibraryService() {
        books.setAll(fileHandler.loadBooks());
        students.setAll(fileHandler.loadStudents());
        history.setAll(fileHandler.loadHistory());
        users.setAll(fileHandler.loadUsers());
        activityLogs.setAll(fileHandler.loadActivity());
        deposits.setAll(fileHandler.loadDeposits());
        requests.setAll(fileHandler.loadRequests());
        saveAll();
    }

    public Optional<User> login(String username, String password) {
        return users.stream()
                .filter(u -> u.username().equalsIgnoreCase(username.trim()) && u.password().equals(password))
                .filter(User::approved)
                .findFirst();
    }

    public ObservableList<Book> getBooks() { return books; }
    public ObservableList<Student> getStudents() { return students; }
    public ObservableList<BorrowRecord> getHistory() { return history; }
    public ObservableList<User> getUsers() { return users; }
    public ObservableList<ActivityLog> getActivityLogs() { return activityLogs; }
    public ObservableList<SecurityDeposit> getDeposits() { return deposits; }
    public ObservableList<BookRequest> getRequests() { return requests; }
    public boolean bookIdExists(String bookId) { return findBook(bookId).isPresent(); }
    public boolean studentIdExists(String studentId) { return findStudent(studentId).isPresent(); }
    public List<School> getSchools() {
        return students.stream()
                .filter(student -> student.getInstitutionType() == InstitutionType.SCHOOL)
                .map(student -> new School(student.getInstitutionName()))
                .distinct()
                .toList();
    }

    public List<College> getColleges() {
        return students.stream()
                .filter(student -> student.getInstitutionType() == InstitutionType.COLLEGE)
                .map(student -> new College(student.getInstitutionName()))
                .distinct()
                .toList();
    }

    public List<University> getUniversities() {
        return students.stream()
                .filter(student -> student.getInstitutionType() == InstitutionType.UNIVERSITY)
                .map(student -> new University(student.getInstitutionName()))
                .distinct()
                .toList();
    }

    public void upsertBook(Book book) {
        upsertBook(book, "System");
    }

    public void upsertBook(Book book, String actor) {
        boolean isNew = findBook(book.getBookId()).isEmpty();
        validateBook(book);
        findBook(book.getBookId()).ifPresentOrElse(existing -> copyBook(book, existing), () -> books.add(book));
        log(actor, isNew ? "Added book" : "Updated book", book.getBookId() + " - " + book.getTitle());
        saveAll();
    }

    public void deleteBook(Book book) {
        boolean activeLoan = history.stream().anyMatch(r -> r.getBookId().equals(book.getBookId()) && r.getReturnDate() == null);
        if (activeLoan) {
            throw new IllegalArgumentException("Book cannot be deleted while it is issued.");
        }
        books.remove(book);
        log("Admin", "Deleted book", book.getBookId() + " - " + book.getTitle());
        saveAll();
    }

    public void upsertStudent(Student student) {
        upsertStudent(student, "System");
    }

    public void upsertStudent(Student student, String actor) {
        boolean isNew = findStudent(student.getStudentId()).isEmpty();
        validateStudent(student);
        findStudent(student.getStudentId()).ifPresentOrElse(existing -> copyStudent(student, existing), () -> students.add(student));
        log(actor, isNew ? "Added student" : "Updated student", student.getStudentId() + " - " + student.getName());
        saveAll();
    }

    public void deleteStudent(Student student) {
        boolean activeLoan = history.stream().anyMatch(r -> r.getStudentId().equals(student.getStudentId()) && r.getReturnDate() == null);
        if (activeLoan) {
            throw new IllegalArgumentException("Student cannot be deleted while a book is issued.");
        }
        students.remove(student);
        log("Admin", "Deleted student", student.getStudentId() + " - " + student.getName());
        saveAll();
    }

    public BorrowRecord issueBook(Student student, Book book, LocalDate issueDate, LocalDate dueDate) {
        return issueBook(student, book, issueDate, dueDate, "Librarian");
    }

    public BorrowRecord issueBook(Student student, Book book, LocalDate issueDate, LocalDate dueDate, String actor) {
        if (student == null || book == null) throw new IllegalArgumentException("Select student and book.");
        SecurityDeposit activeDeposit = activeDepositForStudent(student.getStudentId())
                .orElseThrow(() -> new IllegalArgumentException("Student must submit a verified guarantee / security deposit before issuing a book."));
        if (!hasActiveDeposit(student.getStudentId())) {
            throw new IllegalArgumentException("Student must submit a verified guarantee / security deposit before issuing a book.");
        }
        if (book.getAvailableCopies() <= 0) throw new IllegalArgumentException("No available copies for this book.");
        if (dueDate.isBefore(issueDate)) throw new IllegalArgumentException("Due date cannot be before issue date.");
        long activeCount = history.stream()
                .filter(r -> r.getStudentId().equals(student.getStudentId()) && r.getReturnDate() == null)
                .count();
        if (activeCount >= MAX_ACTIVE_LOANS_PER_STUDENT) {
            throw new IllegalArgumentException("A student can borrow maximum " + MAX_ACTIVE_LOANS_PER_STUDENT + " books at a time.");
        }
        boolean sameBookActive = history.stream().anyMatch(r -> r.getStudentId().equals(student.getStudentId())
                && r.getBookId().equals(book.getBookId()) && r.getReturnDate() == null);
        if (sameBookActive) {
            throw new IllegalArgumentException("This student already borrowed this book and has not returned it.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        BorrowRecord record = new BorrowRecord("BR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                student.getStudentId(), student.getName(), book.getBookId(), book.getTitle(),
                adminIdFor(actor), actor, activeDeposit.getDepositId(), issueDate, dueDate, null, 0, true);
        history.add(record);
        log(actor, "Issued book", record.getBookTitle() + " to " + record.getStudentName());
        saveAll();
        return record;
    }

    public BookRequest requestIssueBook(Student student, Book book) {
        if (student == null) throw new IllegalArgumentException("No student profile is linked to this account.");
        if (book == null) throw new IllegalArgumentException("Select an available book first.");
        if (!hasActiveDeposit(student.getStudentId())) {
            throw new IllegalArgumentException("Guarantee deposit is required before requesting a book.");
        }
        boolean pending = requests.stream().anyMatch(r -> r.getStudentId().equals(student.getStudentId())
                && r.getBookId().equals(book.getBookId()) && r.getRequestType() == RequestType.ISSUE
                && r.getRequestStatus() == RequestStatus.PENDING);
        if (pending) throw new IllegalArgumentException("You already sent a pending issue request for this book.");
        BookRequest request = new BookRequest("REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                student.getStudentId(), student.getName(), book.getBookId(), book.getTitle(), "",
                RequestType.ISSUE, LocalDate.now(), RequestStatus.PENDING);
        requests.add(request);
        log(student.getName(), "Requested book issue", request.getRequestId() + " | " + book.getTitle());
        saveAll();
        return request;
    }

    public BookRequest requestReturnBook(Student student, BorrowRecord record) {
        if (student == null) throw new IllegalArgumentException("No student profile is linked to this account.");
        if (record == null) throw new IllegalArgumentException("Select an active issued book first.");
        boolean pending = requests.stream().anyMatch(r -> r.getIssueId().equals(record.getIssueId())
                && r.getRequestType() == RequestType.RETURN && r.getRequestStatus() == RequestStatus.PENDING);
        if (pending) throw new IllegalArgumentException("You already sent a pending return request for this book.");
        BookRequest request = new BookRequest("REQ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                student.getStudentId(), student.getName(), record.getBookId(), record.getBookTitle(), record.getIssueId(),
                RequestType.RETURN, LocalDate.now(), RequestStatus.PENDING);
        requests.add(request);
        log(student.getName(), "Requested book return", request.getRequestId() + " | " + record.getBookTitle());
        saveAll();
        return request;
    }

    public double returnBook(BorrowRecord record, LocalDate returnDate, boolean finePaid) {
        return returnBook(record, returnDate, finePaid, "Librarian");
    }

    public double returnBook(BorrowRecord record, LocalDate returnDate, boolean finePaid, String actor) {
        if (record == null) throw new IllegalArgumentException("Select an active borrow record.");
        if (record.getReturnDate() != null) throw new IllegalArgumentException("This book is already returned.");
        if (returnDate.isBefore(record.getIssueDate())) throw new IllegalArgumentException("Return date cannot be before issue date.");

        long lateDays = Math.max(0, ChronoUnit.DAYS.between(record.getDueDate(), returnDate));
        double fine = lateDays * FINE_PER_DAY;
        record.setReturnDate(returnDate);
        record.setFine(fine);
        record.setFinePaid(fine == 0 || finePaid);
        findBook(record.getBookId()).ifPresent(book -> book.setAvailableCopies(book.getAvailableCopies() + 1));
        applyDepositRefundAfterReturn(record, fine, actor);
        log(actor, "Returned book", record.getBookTitle() + " from " + record.getStudentName());
        saveAll();
        return fine;
    }

    public void approveRequest(BookRequest request, String actor) {
        if (request == null) throw new IllegalArgumentException("Select a request first.");
        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be approved.");
        }
        if (request.getRequestType() == RequestType.ISSUE) {
            Student student = findStudent(request.getStudentId()).orElseThrow(() -> new IllegalArgumentException("Student not found."));
            Book book = findBook(request.getBookId()).orElseThrow(() -> new IllegalArgumentException("Book not found."));
            issueBook(student, book, LocalDate.now(), LocalDate.now().plusDays(14), actor);
        } else {
            BorrowRecord record = history.stream()
                    .filter(r -> r.getIssueId().equals(request.getIssueId()) && r.getReturnDate() == null)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Active issue record not found."));
            returnBook(record, LocalDate.now(), false, actor);
        }
        request.setRequestStatus(RequestStatus.APPROVED);
        log(actor, "Approved book request", request.getRequestId());
        saveAll();
    }

    public void rejectRequest(BookRequest request, String actor) {
        if (request == null) throw new IllegalArgumentException("Select a request first.");
        if (request.getRequestStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be rejected.");
        }
        request.setRequestStatus(RequestStatus.REJECTED);
        log(actor, "Rejected book request", request.getRequestId());
        saveAll();
    }

    public List<Book> searchBooks(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        if (q.isBlank()) return List.copyOf(books);
        return books.stream()
                .filter(b -> contains(b.getBookId(), q) || contains(b.getIsbn(), q) || contains(b.getTitle(), q)
                        || contains(b.getAuthor(), q) || contains(b.getCategory(), q))
                .toList();
    }

    public int issuedBooksCount() {
        return books.stream().mapToInt(b -> b.getTotalCopies() - b.getAvailableCopies()).sum();
    }

    public int availableBooksCount() {
        return books.stream().mapToInt(Book::getAvailableCopies).sum();
    }

    public List<BorrowRecord> activeLoans() {
        return history.stream().filter(r -> r.getReturnDate() == null).toList();
    }

    public List<BorrowRecord> activeLoansForStudent(String studentId) {
        return history.stream().filter(r -> r.getStudentId().equals(studentId) && r.getReturnDate() == null).toList();
    }

    public List<BorrowRecord> historyForStudent(String studentId) {
        return history.stream().filter(r -> r.getStudentId().equals(studentId)).toList();
    }

    public List<BorrowRecord> unpaidFines() {
        return history.stream().filter(r -> r.getFine() > 0 && !r.isFinePaid()).toList();
    }

    public List<BorrowRecord> overdueLoans() {
        LocalDate today = LocalDate.now();
        return history.stream().filter(r -> r.getReturnDate() == null && r.getDueDate().isBefore(today)).toList();
    }

    public long activeLoanCountForStudent(String studentId) {
        return activeLoansForStudent(studentId).size();
    }

    public boolean hasActiveLoanForBook(String studentId, String bookId) {
        return history.stream().anyMatch(r -> r.getStudentId().equals(studentId)
                && r.getBookId().equals(bookId) && r.getReturnDate() == null);
    }

    public double unpaidFineForStudent(String studentId) {
        return history.stream()
                .filter(r -> r.getStudentId().equals(studentId) && !r.isFinePaid())
                .mapToDouble(BorrowRecord::getFine)
                .sum();
    }

    public double estimateFine(BorrowRecord record, LocalDate returnDate) {
        if (record == null || returnDate == null) return 0;
        long lateDays = Math.max(0, ChronoUnit.DAYS.between(record.getDueDate(), returnDate));
        return lateDays * FINE_PER_DAY;
    }

    public long issuedTodayCount() {
        LocalDate today = LocalDate.now();
        return history.stream().filter(r -> r.getIssueDate().equals(today)).count();
    }

    public long returnedTodayCount() {
        LocalDate today = LocalDate.now();
        return history.stream().filter(r -> today.equals(r.getReturnDate())).count();
    }

    public double fineCollectedToday() {
        LocalDate today = LocalDate.now();
        return activityLogs.stream()
                .filter(log -> log.getTimestamp().toLocalDate().equals(today) && log.getAction().equals("Fine paid"))
                .mapToDouble(log -> {
                    String marker = "Rs. ";
                    int index = log.getDetails().indexOf(marker);
                    try {
                        return index >= 0 ? Double.parseDouble(log.getDetails().substring(index + marker.length())) : 0;
                    } catch (NumberFormatException ex) {
                        return 0;
                    }
                })
                .sum();
    }

    public boolean hasActiveDeposit(String studentId) {
        return deposits.stream().anyMatch(d -> d.getStudentId().equals(studentId) && d.isActiveForIssue());
    }

    public Optional<SecurityDeposit> activeDepositForStudent(String studentId) {
        return deposits.stream().filter(d -> d.getStudentId().equals(studentId) && d.isActiveForIssue()).findFirst();
    }

    public SecurityDeposit submitDeposit(Student student, double amount, LocalDate date, String actor) {
        if (student == null) throw new IllegalArgumentException("Select a student.");
        if (amount <= 0) throw new IllegalArgumentException("Deposit amount must be greater than 0.");
        if (date == null) throw new IllegalArgumentException("Deposit date is required.");
        if (hasActiveDeposit(student.getStudentId())) {
            throw new IllegalArgumentException("This student already has an active verified deposit.");
        }
        boolean pendingRequest = deposits.stream().anyMatch(deposit -> deposit.getStudentId().equals(student.getStudentId())
                && deposit.getDepositStatus() == DepositStatus.PENDING
                && deposit.getRefundStatus() == RefundStatus.NOT_REFUNDED);
        if (pendingRequest) {
            throw new IllegalArgumentException("You already have a pending guarantee request.");
        }
        SecurityDeposit deposit = new SecurityDeposit("DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT),
                student.getStudentId(), amount, date, DepositStatus.PENDING, RefundStatus.NOT_REFUNDED);
        deposits.add(deposit);
        log(actor, "Submitted guarantee deposit", deposit.getDepositId() + " | " + student.getName() + " | Rs. " + amount);
        saveAll();
        return deposit;
    }

    public void updateDepositStatus(SecurityDeposit deposit, DepositStatus status, String actor) {
        if (deposit == null) throw new IllegalArgumentException("Select a deposit record.");
        deposit.setDepositStatus(status);
        log(actor, "Updated deposit status", deposit.getDepositId() + " | " + status);
        saveAll();
    }

    public void refundDeposit(SecurityDeposit deposit, RefundStatus refundStatus, String actor) {
        if (deposit == null) throw new IllegalArgumentException("Select a deposit record.");
        if (refundStatus == RefundStatus.NOT_REFUNDED) {
            throw new IllegalArgumentException("Select a refund result.");
        }
        boolean activeLoan = activeLoansForStudent(deposit.getStudentId()).size() > 0;
        if (activeLoan) {
            throw new IllegalArgumentException("Deposit cannot be refunded while the student has active loans.");
        }
        deposit.setRefundStatus(refundStatus);
        log(actor, "Updated deposit refund", deposit.getDepositId() + " | " + refundStatus);
        saveAll();
    }

    public double unpaidFineTotal() {
        return history.stream().filter(r -> !r.isFinePaid()).mapToDouble(BorrowRecord::getFine).sum();
    }

    public void markFinePaid(BorrowRecord record) {
        markFinePaid(record, "Admin");
    }

    public void markFinePaid(BorrowRecord record, String actor) {
        if (record == null) throw new IllegalArgumentException("Select a fine record.");
        if (record.getFine() <= 0) throw new IllegalArgumentException("This record has no fine.");
        record.setFinePaid(true);
        log(actor, "Fine paid", record.getRecordId() + " | Rs. " + record.getFine());
        saveAll();
    }

    public void registerStudentAccount(Student student, String username, String password, String confirmPassword) {
        validateStudent(student);
        validateCredentials(username, password, confirmPassword);
        if (findStudent(student.getStudentId()).isPresent()) {
            throw new IllegalArgumentException("Duplicate student ID already exists.");
        }
        if (findUser(username).isPresent()) {
            throw new IllegalArgumentException("Duplicate username already exists.");
        }
        students.add(student);
        users.add(new User(username.trim(), password, Role.STUDENT, student.getName(), student.getStudentId(), false));
        log("Registration", "Student account requested", student.getStudentId() + " - " + student.getName());
        saveAll();
    }

    public void approveUser(User user, boolean approved) {
        int index = users.indexOf(user);
        if (index >= 0) {
            users.set(index, new User(user.username(), user.password(), user.role(), user.displayName(), user.studentId(), approved));
            log("Admin", approved ? "Approved user" : "Blocked user", user.username());
            saveAll();
        }
    }

    public Optional<Student> studentForUser(User user) {
        if (user == null || user.studentId().isBlank()) return Optional.empty();
        return findStudent(user.studentId());
    }

    public List<String> bookSuggestions(String query) {
        String q = query.toLowerCase(Locale.ROOT).trim();
        if (q.isBlank()) return List.of();
        return books.stream()
                .flatMap(b -> List.of(b.getTitle(), b.getAuthor(), b.getIsbn(), b.getCategory()).stream())
                .filter(value -> contains(value, q))
                .distinct()
                .limit(6)
                .toList();
    }

    private Optional<Book> findBook(String bookId) {
        return books.stream().filter(b -> b.getBookId().equalsIgnoreCase(bookId)).findFirst();
    }

    private Optional<Student> findStudent(String studentId) {
        return students.stream().filter(s -> s.getStudentId().equalsIgnoreCase(studentId)).findFirst();
    }

    private Optional<User> findUser(String username) {
        return users.stream().filter(u -> u.username().equalsIgnoreCase(username.trim())).findFirst();
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(q);
    }

    private boolean blank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void validateBook(Book book) {
        if (blank(book.getBookId()) || blank(book.getIsbn()) || blank(book.getTitle()) || blank(book.getAuthor())
                || blank(book.getPublisher()) || blank(book.getCategory()) || blank(book.getLanguage())
                || blank(book.getShelfNo()) || blank(book.getRackNo())) {
            throw new IllegalArgumentException("All book fields are required.");
        }
        if (book.getAvailableCopies() > book.getTotalCopies()) {
            throw new IllegalArgumentException("Available copies cannot be greater than total copies.");
        }
        if (book.getTotalCopies() <= 0) {
            throw new IllegalArgumentException("Total copies must be greater than 0.");
        }
        long duplicates = books.stream().filter(b -> b.getBookId().equalsIgnoreCase(book.getBookId())).count();
        if (duplicates > 1) {
            throw new IllegalArgumentException("Duplicate book ID already exists.");
        }
    }

    private void validateStudent(Student student) {
        if (blank(student.getStudentId()) || blank(student.getName()) || student.getInstitutionType() == null
                || blank(student.getAcademicGroup()) || blank(student.getAcademicLevel())
                || blank(student.getInstitutionName()) || blank(student.getAddress()) || blank(student.getPhone())
                || blank(student.getEmail())) {
            throw new IllegalArgumentException("All student fields are required.");
        }
        if (!EMAIL.matcher(student.getEmail().trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        if (!PHONE.matcher(student.getPhone().trim()).matches()) {
            throw new IllegalArgumentException("Enter a valid phone number.");
        }
        long duplicates = students.stream().filter(s -> s.getStudentId().equalsIgnoreCase(student.getStudentId())).count();
        if (duplicates > 1) {
            throw new IllegalArgumentException("Duplicate student ID already exists.");
        }
    }

    private void validateCredentials(String username, String password, String confirmPassword) {
        if (blank(username) || blank(password) || blank(confirmPassword)) {
            throw new IllegalArgumentException("Username, password and confirm password are required.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirm password do not match.");
        }
        if (password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        }
    }

    private void copyBook(Book source, Book target) {
        target.setIsbn(source.getIsbn());
        target.setTitle(source.getTitle());
        target.setAuthor(source.getAuthor());
        target.setPublisher(source.getPublisher());
        target.setCategory(source.getCategory());
        target.setLanguage(source.getLanguage());
        target.setShelfNo(source.getShelfNo());
        target.setRackNo(source.getRackNo());
        target.setTotalCopies(source.getTotalCopies());
        target.setAvailableCopies(source.getAvailableCopies());
    }

    private void copyStudent(Student source, Student target) {
        target.setName(source.getName());
        target.setInstitutionType(source.getInstitutionType());
        target.setAcademicGroup(source.getAcademicGroup());
        target.setAcademicLevel(source.getAcademicLevel());
        target.setInstitutionName(source.getInstitutionName());
        target.setAddress(source.getAddress());
        target.setPhone(source.getPhone());
        target.setEmail(source.getEmail());
    }

    private void saveAll() {
        fileHandler.saveBooks(books);
        fileHandler.saveStudents(students);
        fileHandler.saveHistory(history);
        fileHandler.saveUsers(users);
        fileHandler.saveActivity(activityLogs);
        fileHandler.saveDeposits(deposits);
        fileHandler.saveRequests(requests);
    }

    private void log(String actor, String action, String details) {
        activityLogs.add(new ActivityLog(LocalDateTime.now(), actor, action, details));
    }

    private String adminIdFor(String actor) {
        return users.stream()
                .filter(user -> user.displayName().equalsIgnoreCase(actor) || user.username().equalsIgnoreCase(actor))
                .findFirst()
                .map(user -> user.role() == Role.ADMIN ? "ADM-" + user.username().toUpperCase(Locale.ROOT)
                        : "STAFF-" + user.username().toUpperCase(Locale.ROOT))
                .orElse("ADM-001");
    }

    private void applyDepositRefundAfterReturn(BorrowRecord record, double fine, String actor) {
        if (!activeLoansForStudent(record.getStudentId()).isEmpty()) {
            return;
        }
        deposits.stream()
                .filter(deposit -> deposit.getDepositId().equals(record.getDepositId()))
                .findFirst()
                .ifPresent(deposit -> {
                    if (fine > 0) {
                        deposit.setRefundStatus(RefundStatus.DEDUCTED_FOR_FINE);
                        record.setFinePaid(true);
                        log(actor, "Deposit deducted for fine", deposit.getDepositId() + " | Rs. " + fine);
                    } else {
                        deposit.setRefundStatus(RefundStatus.REFUNDED);
                        log(actor, "Deposit refunded", deposit.getDepositId());
                    }
                });
    }
}
