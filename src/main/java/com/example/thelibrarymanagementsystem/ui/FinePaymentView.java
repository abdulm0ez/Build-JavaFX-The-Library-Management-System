package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
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

public class FinePaymentView extends VBox {
    private final LibraryService service;
    private final User user;
    private final TableView<BorrowRecord> table = new TableView<>();

    public FinePaymentView(LibraryService service, User user) {
        this.service = service;
        this.user = user;
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Fine Payment Management");
        title.getStyleClass().add("page-title");
        setup();
        Button paid = new Button("Mark Fine Paid");
        paid.getStyleClass().add("primary-button");
        paid.setOnAction(event -> markPaid());
        getChildren().addAll(title, new HBox(10, paid), table);
        VBox.setVgrow(table, Priority.ALWAYS);
        refresh();
    }

    private void setup() {
        add("Record ID", "recordId", 110);
        add("Student", "studentName", 150);
        add("Book", "bookTitle", 220);
        add("Due Date", "dueDate", 105);
        add("Return Date", "returnDate", 105);
        add("Fine", "fine", 80);
        add("Fine Status", "fineStatus", 100);
    }

    private void add(String title, String property, int width) {
        TableColumn<BorrowRecord, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void markPaid() {
        try {
            service.markFinePaid(table.getSelectionModel().getSelectedItem(), user.displayName());
            refresh();
            Alerts.info("Fine Paid", "Fine marked as paid.");
        } catch (RuntimeException ex) {
            Alerts.error("Fine Payment Failed", ex.getMessage());
        }
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(service.unpaidFines()));
    }
}
