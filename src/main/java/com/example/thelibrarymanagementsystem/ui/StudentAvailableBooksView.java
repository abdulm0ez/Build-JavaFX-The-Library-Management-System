package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentAvailableBooksView extends VBox {
    public StudentAvailableBooksView(LibraryService service, User user) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Available Books");
        title.getStyleClass().add("page-title");
        Student student = service.studentForUser(user).orElse(null);
        Label deposit = new Label(student != null && service.hasActiveDeposit(student.getStudentId())
                ? "Guarantee Deposit: VERIFIED"
                : "Guarantee Deposit: REQUIRED before issuing a book");
        deposit.getStyleClass().add(student != null && service.hasActiveDeposit(student.getStudentId()) ? "section-title" : "warning-text");

        TableView<Book> table = new TableView<>();
        add(table, "Book ID", "bookId", 90);
        add(table, "Title", "title", 220);
        add(table, "Author", "author", 150);
        add(table, "Category", "category", 120);
        add(table, "ISBN", "isbn", 130);
        add(table, "Available", "availableCopies", 90);
        add(table, "Status", "availabilityStatus", 100);
        table.setItems(FXCollections.observableArrayList(service.getBooks().stream()
                .filter(book -> book.getAvailableCopies() > 0)
                .toList()));
        table.setPlaceholder(new Label("No available books right now."));
        getChildren().addAll(title, deposit, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void add(TableView<Book> table, String title, String property, int width) {
        TableColumn<Book, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
