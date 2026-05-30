package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
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

import java.time.LocalDate;

public class StudentReturnBookView extends VBox {
    private final TableView<BorrowRecord> table = new TableView<>();

    public StudentReturnBookView(LibraryService service, User user) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Return Book");
        title.getStyleClass().add("page-title");
        Student student = service.studentForUser(user).orElse(null);
        setup();
        refresh(service, student);
        Button returnBook = new Button("Request Return");
        returnBook.getStyleClass().add("primary-button");
        returnBook.setOnAction(event -> {
            try {
                BorrowRecord record = table.getSelectionModel().getSelectedItem();
                if (record == null) throw new IllegalArgumentException("Select an active issued book first.");
                var request = service.requestReturnBook(student, record);
                refresh(service, student);
                Alerts.info("Return Request Sent", "Request ID: " + request.getRequestId()
                        + "\nBook: " + request.getBookTitle()
                        + "\nWait for librarian approval.");
            } catch (RuntimeException ex) {
                Alerts.error("Return Failed", ex.getMessage());
            }
        });
        getChildren().addAll(title, new HBox(10, returnBook), table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setup() {
        add("Issue ID", "issueId", 110);
        add("Book ID", "bookId", 90);
        add("Book", "bookTitle", 220);
        add("Issue Date", "issueDate", 105);
        add("Due Date", "dueDate", 105);
        add("Fine", "fine", 80);
        add("Status", "issueStatus", 100);
        table.setPlaceholder(new Label("No active issued books to return."));
    }

    private void add(String title, String property, int width) {
        TableColumn<BorrowRecord, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void refresh(LibraryService service, Student student) {
        table.setItems(FXCollections.observableArrayList(student == null
                ? java.util.List.of()
                : service.activeLoansForStudent(student.getStudentId())));
    }
}
