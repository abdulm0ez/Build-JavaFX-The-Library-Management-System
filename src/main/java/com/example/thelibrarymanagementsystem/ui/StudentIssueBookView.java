package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.model.BookRequest;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentIssueBookView extends VBox {
    private final TableView<Book> table = new TableView<>();

    public StudentIssueBookView(LibraryService service, User user) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Issue Book");
        title.getStyleClass().add("page-title");
        Student student = service.studentForUser(user).orElse(null);
        Label note = new Label("Select an available book and send a request. Librarian will approve and give the book.");
        note.getStyleClass().add("muted");
        Label deposit = new Label(student != null && service.hasActiveDeposit(student.getStudentId())
                ? "Guarantee Deposit: VERIFIED"
                : "Guarantee Deposit: REQUIRED - admin must submit and verify deposit first.");
        deposit.getStyleClass().add(student != null && service.hasActiveDeposit(student.getStudentId()) ? "section-title" : "warning-text");

        setup();
        refresh(service);
        Button issue = new Button("Request Issue");
        issue.getStyleClass().add("primary-button");
        issue.setOnAction(event -> {
            try {
                if (student == null) throw new IllegalArgumentException("No student profile is linked to this account.");
                Book book = table.getSelectionModel().getSelectedItem();
                if (book == null) throw new IllegalArgumentException("Select an available book first.");
                BookRequest request = service.requestIssueBook(student, book);
                refresh(service);
                Alerts.info("Issue Request Sent", "Request ID: " + request.getRequestId()
                        + "\nBook: " + request.getBookTitle()
                        + "\nWait for librarian approval.");
            } catch (RuntimeException ex) {
                Alerts.error("Issue Failed", ex.getMessage());
            }
        });
        getChildren().addAll(title, note, deposit, new HBox(10, issue), table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setup() {
        add("Book ID", "bookId", 90);
        add("Title", "title", 220);
        add("Author", "author", 150);
        add("Category", "category", 120);
        add("ISBN", "isbn", 130);
        add("Available", "availableCopies", 90);
        add("Status", "availabilityStatus", 100);
        table.setPlaceholder(new Label("No available books right now."));
    }

    private void add(String title, String property, int width) {
        TableColumn<Book, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void refresh(LibraryService service) {
        table.setItems(FXCollections.observableArrayList(service.getBooks().stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .toList()));
    }
}
