package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BookRequest;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BookRequestsView extends VBox {
    private final TableView<BookRequest> table = new TableView<>();

    public BookRequestsView(LibraryService service, String actor) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Book Requests");
        title.getStyleClass().add("page-title");
        setup();
        table.setItems(service.getRequests());
        table.setPlaceholder(new Label("No book requests yet."));

        Button approve = new Button("Approve Request");
        Button reject = new Button("Reject Request");
        approve.getStyleClass().add("primary-button");
        reject.getStyleClass().add("danger-button");
        approve.setOnAction(event -> {
            try {
                service.approveRequest(table.getSelectionModel().getSelectedItem(), actor);
                table.refresh();
                Alerts.info("Request Approved", "Request approved and processed.");
            } catch (RuntimeException ex) {
                Alerts.error("Approval Failed", ex.getMessage());
            }
        });
        reject.setOnAction(event -> {
            try {
                service.rejectRequest(table.getSelectionModel().getSelectedItem(), actor);
                table.refresh();
                Alerts.info("Request Rejected", "Request rejected.");
            } catch (RuntimeException ex) {
                Alerts.error("Reject Failed", ex.getMessage());
            }
        });

        getChildren().addAll(title, new HBox(10, approve, reject), table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setup() {
        add("Request ID", "requestId", 110);
        add("Type", "requestType", 90);
        add("Status", "requestStatus", 100);
        add("Student ID", "studentId", 100);
        add("Student", "studentName", 150);
        add("Book ID", "bookId", 90);
        add("Book", "bookTitle", 220);
        add("Issue ID", "issueId", 110);
        add("Request Date", "requestDate", 110);
    }

    private void add(String title, String property, int width) {
        TableColumn<BookRequest, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
