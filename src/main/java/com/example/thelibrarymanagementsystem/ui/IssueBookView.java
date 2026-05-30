package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Locale;

public class IssueBookView extends VBox {
    private final User user;

    public IssueBookView(LibraryService service, User user) {
        this.user = user;
        setSpacing(18);
        setPadding(new Insets(26));
        Label title = new Label("Issue Book");
        title.getStyleClass().add("page-title");

        TextField studentSearch = new TextField();
        studentSearch.setPromptText("Search student by ID or name");
        TextField bookSearch = new TextField();
        bookSearch.setPromptText("Search book by title, ISBN or book ID");
        ComboBox<Student> students = new ComboBox<>(service.getStudents());
        ComboBox<Book> books = new ComboBox<>(service.getBooks().filtered(b -> b.getAvailableCopies() > 0));
        students.setPromptText("Select student");
        books.setPromptText("Select available book");
        students.setMaxWidth(Double.MAX_VALUE);
        books.setMaxWidth(Double.MAX_VALUE);
        books.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getBookId() + " - " + item.getTitle() + " (" + item.getAvailableCopies() + ")");
            }
        });
        books.setButtonCell(books.getCellFactory().call(null));
        studentSearch.textProperty().addListener((obs, old, value) -> students.setItems(FXCollections.observableArrayList(
                service.getStudents().stream().filter(student -> matches(value, student.getStudentId(), student.getName())).toList())));
        bookSearch.textProperty().addListener((obs, old, value) -> books.setItems(FXCollections.observableArrayList(
                service.getBooks().stream().filter(book -> book.getAvailableCopies() > 0)
                        .filter(book -> matches(value, book.getBookId(), book.getIsbn(), book.getTitle())).toList())));

        DatePicker issueDate = new DatePicker(LocalDate.now());
        DatePicker dueDate = new DatePicker(LocalDate.now().plusDays(14));
        Button issue = new Button("Issue Book");
        issue.getStyleClass().add("primary-button");
        Button profile = new Button("Student Profile");
        profile.getStyleClass().add("soft-button");
        Label availability = new Label("Select a book to see available copies.");
        Label warnings = new Label();
        warnings.getStyleClass().add("warning-text");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-panel");
        form.setHgap(12);
        form.setVgap(14);
        form.setPadding(new Insets(22));
        form.addRow(0, new Label("Find Student"), studentSearch);
        form.addRow(1, new Label("Student"), students);
        form.addRow(2, new Label("Find Book"), bookSearch);
        form.addRow(3, new Label("Book"), books);
        form.addRow(4, new Label("Issue Date"), issueDate);
        form.addRow(5, new Label("Due Date"), dueDate);
        form.add(availability, 1, 6);
        form.add(warnings, 1, 7);
        form.add(new HBox(10, issue, profile), 1, 8);

        students.valueProperty().addListener((obs, old, value) -> updateWarnings(service, students.getValue(), books.getValue(), warnings));
        books.valueProperty().addListener((obs, old, value) -> {
            availability.setText(value == null ? "Select a book to see available copies."
                    : "Available copies: " + value.getAvailableCopies());
            updateWarnings(service, students.getValue(), books.getValue(), warnings);
        });
        profile.setOnAction(event -> {
            if (students.getValue() == null) {
                Alerts.error("No Student Selected", "Select a student first.");
                return;
            }
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Student Quick Profile");
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            dialog.getDialogPane().setContent(new StudentProfileView(service, students.getValue()));
            dialog.getDialogPane().setPrefSize(820, 560);
            dialog.showAndWait();
        });

        issue.setOnAction(event -> {
            try {
                var record = service.issueBook(students.getValue(), books.getValue(), issueDate.getValue(), dueDate.getValue(), user.displayName());
                books.setItems(service.getBooks().filtered(b -> b.getAvailableCopies() > 0));
                Alerts.info("Issue Receipt", "Record ID: " + record.getRecordId()
                        + "\nStudent: " + record.getStudentName()
                        + "\nBook: " + record.getBookTitle()
                        + "\nIssue Date: " + record.getIssueDate()
                        + "\nDue Date: " + record.getDueDate()
                        + "\nFine: Rs. 0.0");
            } catch (RuntimeException ex) {
                Alerts.error("Issue Failed", ex.getMessage());
            }
        });

        getChildren().addAll(title, form);
    }

    private void updateWarnings(LibraryService service, Student student, Book book, Label warnings) {
        if (student == null) {
            warnings.setText("");
            return;
        }
        StringBuilder text = new StringBuilder();
        long active = service.activeLoanCountForStudent(student.getStudentId());
        double unpaidFine = service.unpaidFineForStudent(student.getStudentId());
        if (active >= LibraryService.MAX_ACTIVE_LOANS_PER_STUDENT) {
            text.append("Warning: student reached max borrow limit. ");
        }
        if (unpaidFine > 0) {
            text.append("Warning: unpaid fine Rs. ").append(unpaidFine).append(". ");
        }
        if (!service.hasActiveDeposit(student.getStudentId())) {
            text.append("Warning: verified guarantee deposit required. ");
        }
        if (!service.overdueLoans().stream().filter(r -> r.getStudentId().equals(student.getStudentId())).toList().isEmpty()) {
            text.append("Warning: student has overdue books. ");
        }
        if (book != null) {
            if (book.getAvailableCopies() == 1) {
                text.append("Warning: only 1 copy left. ");
            }
            if (service.hasActiveLoanForBook(student.getStudentId(), book.getBookId())) {
                text.append("Warning: student already borrowed this book. ");
            }
        }
        warnings.setText(text.toString());
    }

    private boolean matches(String query, String... values) {
        String q = query == null ? "" : query.toLowerCase(Locale.ROOT).trim();
        if (q.isBlank()) return true;
        for (String value : values) {
            if (value != null && value.toLowerCase(Locale.ROOT).contains(q)) {
                return true;
            }
        }
        return false;
    }
}
