package com.example.thelibrarymanagementsystem.util;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.model.ActivityLog;
import com.example.thelibrarymanagementsystem.model.Role;
import com.example.thelibrarymanagementsystem.model.InstitutionType;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.model.DepositStatus;
import com.example.thelibrarymanagementsystem.model.RefundStatus;
import com.example.thelibrarymanagementsystem.model.SecurityDeposit;
import com.example.thelibrarymanagementsystem.model.BookRequest;
import com.example.thelibrarymanagementsystem.model.RequestStatus;
import com.example.thelibrarymanagementsystem.model.RequestType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private final Path dataDir = Path.of("data");
    private final Path booksFile = dataDir.resolve("books.csv");
    private final Path studentsFile = dataDir.resolve("students.csv");
    private final Path historyFile = dataDir.resolve("history.csv");
    private final Path usersFile = dataDir.resolve("users.csv");
    private final Path activityFile = dataDir.resolve("activity-log.csv");
    private final Path depositsFile = dataDir.resolve("deposits.csv");
    private final Path requestsFile = dataDir.resolve("book-requests.csv");
    private final Path backupDir = dataDir.resolve("backup");

    public FileHandler() {
        try {
            Files.createDirectories(dataDir);
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create data directory", e);
        }
    }

    public List<Book> loadBooks() {
        if (!Files.exists(booksFile)) {
            return sampleBooks();
        }
        try {
            List<Book> books = new ArrayList<>();
            for (String line : Files.readAllLines(booksFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                books.add(new Book(u(p[0]), u(p[1]), u(p[2]), u(p[3]), u(p[4]), u(p[5]), u(p[6]),
                        u(p[7]), u(p[8]), Integer.parseInt(p[9]), Integer.parseInt(p[10])));
            }
            return books;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read books", e);
        }
    }

    public void saveBooks(List<Book> books) {
        List<String> lines = books.stream()
                .map(b -> String.join(",", e(b.getBookId()), e(b.getIsbn()), e(b.getTitle()), e(b.getAuthor()),
                        e(b.getPublisher()), e(b.getCategory()), e(b.getLanguage()), e(b.getShelfNo()),
                        e(b.getRackNo()), String.valueOf(b.getTotalCopies()), String.valueOf(b.getAvailableCopies())))
                .toList();
        write(booksFile, lines);
    }

    public List<Student> loadStudents() {
        if (!Files.exists(studentsFile)) {
            return sampleStudents();
        }
        try {
            List<Student> students = new ArrayList<>();
            for (String line : Files.readAllLines(studentsFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 9) {
                    students.add(new Student(u(p[0]), u(p[1]), InstitutionType.fromLabel(p[2]), u(p[3]),
                            u(p[4]), u(p[5]), u(p[6]), u(p[7]), u(p[8])));
                } else if (p.length >= 7) {
                    students.add(new Student(u(p[0]), u(p[1]), InstitutionType.fromLabel(p[3]), u(p[2]),
                            "", u(p[3]) + " Campus", u(p[4]), u(p[5]), u(p[6])));
                } else {
                    students.add(new Student(u(p[0]), u(p[1]), InstitutionType.UNIVERSITY, u(p[2]),
                            "", "University Campus", "", u(p[4]), u(p[5])));
                }
            }
            return students;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read students", e);
        }
    }

    public void saveStudents(List<Student> students) {
        List<String> lines = students.stream()
                .map(s -> String.join(",", e(s.getStudentId()), e(s.getName()), s.getInstitutionType().name(),
                        e(s.getAcademicGroup()), e(s.getAcademicLevel()), e(s.getInstitutionName()),
                        e(s.getAddress()), e(s.getPhone()), e(s.getEmail())))
                .toList();
        write(studentsFile, lines);
    }

    public List<BorrowRecord> loadHistory() {
        if (!Files.exists(historyFile)) {
            return new ArrayList<>();
        }
        try {
            List<BorrowRecord> records = new ArrayList<>();
            for (String line : Files.readAllLines(historyFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length >= 13) {
                    records.add(new BorrowRecord(u(p[0]), u(p[1]), u(p[2]), u(p[3]), u(p[4]),
                            u(p[5]), u(p[6]), u(p[7]), LocalDate.parse(p[8]), LocalDate.parse(p[9]),
                            p[10].isBlank() ? null : LocalDate.parse(p[10]), Double.parseDouble(p[11]),
                            Boolean.parseBoolean(p[12])));
                } else {
                    records.add(new BorrowRecord(u(p[0]), u(p[1]), u(p[2]), u(p[3]), u(p[4]),
                            LocalDate.parse(p[5]), LocalDate.parse(p[6]), p[7].isBlank() ? null : LocalDate.parse(p[7]),
                            Double.parseDouble(p[8]), Boolean.parseBoolean(p[9])));
                }
            }
            return records;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read borrow history", e);
        }
    }

    public void saveHistory(List<BorrowRecord> records) {
        List<String> lines = records.stream()
                .map(r -> String.join(",", e(r.getRecordId()), e(r.getStudentId()), e(r.getStudentName()),
                        e(r.getBookId()), e(r.getBookTitle()), e(r.getAdminId()), e(r.getAdminName()),
                        e(r.getDepositId()), r.getIssueDate().toString(), r.getDueDate().toString(),
                        r.getReturnDate() == null ? "" : r.getReturnDate().toString(),
                        String.valueOf(r.getFine()), String.valueOf(r.isFinePaid())))
                .toList();
        write(historyFile, lines);
    }

    public List<User> loadUsers() {
        if (!Files.exists(usersFile)) {
            return sampleUsers();
        }
        try {
            List<User> users = new ArrayList<>();
            for (String line : Files.readAllLines(usersFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                users.add(new User(u(p[0]), u(p[1]), Role.valueOf(p[2]), u(p[3]), u(p[4]), Boolean.parseBoolean(p[5])));
            }
            return users;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read users", e);
        }
    }

    public void saveUsers(List<User> users) {
        List<String> lines = users.stream()
                .map(u -> String.join(",", e(u.username()), e(u.password()), u.role().name(), e(u.displayName()),
                        e(u.studentId()), String.valueOf(u.approved())))
                .toList();
        write(usersFile, lines);
    }

    public List<ActivityLog> loadActivity() {
        if (!Files.exists(activityFile)) {
            return new ArrayList<>();
        }
        try {
            List<ActivityLog> logs = new ArrayList<>();
            for (String line : Files.readAllLines(activityFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                logs.add(new ActivityLog(LocalDateTime.parse(p[0]), u(p[1]), u(p[2]), u(p[3])));
            }
            return logs;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read activity log", e);
        }
    }

    public void saveActivity(List<ActivityLog> logs) {
        List<String> lines = logs.stream()
                .map(log -> String.join(",", log.getTimestamp().toString(), e(log.getActor()), e(log.getAction()), e(log.getDetails())))
                .toList();
        write(activityFile, lines);
    }

    public List<SecurityDeposit> loadDeposits() {
        if (!Files.exists(depositsFile)) {
            return new ArrayList<>();
        }
        try {
            List<SecurityDeposit> deposits = new ArrayList<>();
            for (String line : Files.readAllLines(depositsFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                deposits.add(new SecurityDeposit(u(p[0]), u(p[1]), Double.parseDouble(p[2]),
                        LocalDate.parse(p[3]), DepositStatus.valueOf(p[4]), RefundStatus.valueOf(p[5])));
            }
            return deposits;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read security deposits", e);
        }
    }

    public void saveDeposits(List<SecurityDeposit> deposits) {
        List<String> lines = deposits.stream()
                .map(d -> String.join(",", e(d.getDepositId()), e(d.getStudentId()),
                        String.valueOf(d.getDepositAmount()), d.getDepositDate().toString(),
                        d.getDepositStatus().name(), d.getRefundStatus().name()))
                .toList();
        write(depositsFile, lines);
    }

    public List<BookRequest> loadRequests() {
        if (!Files.exists(requestsFile)) {
            return new ArrayList<>();
        }
        try {
            List<BookRequest> requests = new ArrayList<>();
            for (String line : Files.readAllLines(requestsFile, StandardCharsets.UTF_8)) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                requests.add(new BookRequest(u(p[0]), u(p[1]), u(p[2]), u(p[3]), u(p[4]), u(p[5]),
                        RequestType.valueOf(p[6]), LocalDate.parse(p[7]), RequestStatus.valueOf(p[8])));
            }
            return requests;
        } catch (IOException e) {
            throw new IllegalStateException("Could not read book requests", e);
        }
    }

    public void saveRequests(List<BookRequest> requests) {
        List<String> lines = requests.stream()
                .map(r -> String.join(",", e(r.getRequestId()), e(r.getStudentId()), e(r.getStudentName()),
                        e(r.getBookId()), e(r.getBookTitle()), e(r.getIssueId()), r.getRequestType().name(),
                        r.getRequestDate().toString(), r.getRequestStatus().name()))
                .toList();
        write(requestsFile, lines);
    }

    private void write(Path file, List<String> lines) {
        try {
            backup(file);
            Files.write(file, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Could not write " + file.getFileName(), e);
        }
    }

    private void backup(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Files.copy(file, backupDir.resolve(file.getFileName() + "." + stamp + ".bak"));
    }

    private List<Book> sampleBooks() {
        return new ArrayList<>(List.of(
                new Book("B-1001", "9780134685991", "Effective Java", "Joshua Bloch", "Addison-Wesley", "Programming", "English", "S1", "R1", 6, 6),
                new Book("B-1002", "9781492078005", "Head First Java", "Kathy Sierra", "O'Reilly", "Programming", "English", "S1", "R2", 5, 5),
                new Book("B-1003", "9780135166307", "Core Java Volume I", "Cay Horstmann", "Pearson", "Programming", "English", "S2", "R1", 4, 4),
                new Book("B-1004", "9780321356680", "Java Concurrency in Practice", "Brian Goetz", "Addison-Wesley", "Advanced Java", "English", "S2", "R4", 3, 3),
                new Book("B-1005", "9781617294945", "Spring in Action", "Craig Walls", "Manning", "Frameworks", "English", "S3", "R2", 4, 4)
        ));
    }

    private List<Student> sampleStudents() {
        return new ArrayList<>(List.of(
                new Student("ST-2201", "Ayesha Khan", InstitutionType.UNIVERSITY, "Computer Science", "4th Year", "National Tech University", "Block A, Main Campus", "0300-1234567", "ayesha@campus.edu"),
                new Student("ST-2202", "Hamza Ali", InstitutionType.COLLEGE, "Pre-Engineering", "2nd Year", "City Science College", "College Road Campus", "0301-4567890", "hamza@campus.edu"),
                new Student("ST-2203", "Mina Joseph", InstitutionType.SCHOOL, "10", "A", "North City School", "North City Branch", "0302-9876543", "mina@campus.edu")
        ));
    }

    private List<User> sampleUsers() {
        return new ArrayList<>(List.of(
                new User("admin", "admin123", Role.ADMIN, "Admin Officer", "", true),
                new User("librarian", "lib123", Role.LIBRARIAN, "Librarian Desk", "", true)
        ));
    }

    private String e(String value) {
        return value == null ? "" : value.replace(",", "&#44;");
    }

    private String u(String value) {
        return value == null ? "" : value.replace("&#44;", ",");
    }
}
