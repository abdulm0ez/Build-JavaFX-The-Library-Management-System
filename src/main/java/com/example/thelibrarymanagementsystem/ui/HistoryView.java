package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class HistoryView extends VBox {
    public HistoryView(LibraryService service) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Borrow History");
        title.getStyleClass().add("page-title");
        TableView<BorrowRecord> table = new TableView<>(service.getHistory());
        add(table, "Record ID", "recordId", 110);
        add(table, "Student ID", "studentId", 100);
        add(table, "Student", "studentName", 150);
        add(table, "Book ID", "bookId", 90);
        add(table, "Book", "bookTitle", 200);
        add(table, "Issue Date", "issueDate", 105);
        add(table, "Due Date", "dueDate", 105);
        add(table, "Return Date", "returnDate", 105);
        add(table, "Fine", "fine", 80);
        add(table, "Fine Status", "fineStatus", 100);
        add(table, "Status", "status", 100);
        getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void add(TableView<BorrowRecord> table, String title, String property, int width) {
        TableColumn<BorrowRecord, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
