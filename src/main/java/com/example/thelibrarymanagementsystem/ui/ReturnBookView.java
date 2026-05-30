package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.Locale;

public class ReturnBookView extends VBox {
    private final TableView<BorrowRecord> table = new TableView<>();
    private final ObservableList<BorrowRecord> activeLoans;
    private final FilteredList<BorrowRecord> filteredLoans;

    private final User user;

    public ReturnBookView(LibraryService service, User user) {
        this.user = user;
        this.activeLoans = FXCollections.observableArrayList(service.activeLoans());
        this.filteredLoans = new FilteredList<>(activeLoans, record -> true);
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Return Book");
        title.getStyleClass().add("page-title");

        setup();
        table.setItems(filteredLoans);
        TextField search = new TextField();
        search.setPromptText("Search record ID, student ID, student name or book title");
        search.textProperty().addListener((obs, old, value) -> filteredLoans.setPredicate(record -> matches(value,
                record.getRecordId(), record.getStudentId(), record.getStudentName(), record.getBookTitle())));
        DatePicker returnDate = new DatePicker(LocalDate.now());
        CheckBox finePaid = new CheckBox("Fine paid now");
        finePaid.setSelected(true);
        Label finePreview = new Label("Estimated fine: Rs. 0.0");
        finePreview.getStyleClass().add("warning-text");
        Button returnButton = new Button("Return Selected");
        returnButton.getStyleClass().add("primary-button");
        table.getSelectionModel().selectedItemProperty().addListener((obs, old, value) ->
                finePreview.setText("Estimated fine: Rs. " + service.estimateFine(value, returnDate.getValue())));
        returnDate.valueProperty().addListener((obs, old, value) ->
                finePreview.setText("Estimated fine: Rs. " + service.estimateFine(table.getSelectionModel().getSelectedItem(), value)));
        returnButton.setOnAction(event -> {
            try {
                BorrowRecord selected = table.getSelectionModel().getSelectedItem();
                double fine = service.returnBook(selected, returnDate.getValue(), finePaid.isSelected(), user.displayName());
                refreshHistory(service);
                Alerts.info("Return Receipt", "Record ID: " + selected.getRecordId()
                        + "\nStudent: " + selected.getStudentName()
                        + "\nBook: " + selected.getBookTitle()
                        + "\nIssue Date: " + selected.getIssueDate()
                        + "\nDue Date: " + selected.getDueDate()
                        + "\nReturn Date: " + selected.getReturnDate()
                        + "\nFine: Rs. " + fine
                        + "\nFine Status: " + selected.getFineStatus());
            } catch (RuntimeException ex) {
                Alerts.error("Return Failed", ex.getMessage());
            }
        });

        HBox controls = new HBox(12, new Label("Return Date"), returnDate, finePaid, returnButton, finePreview);
        getChildren().addAll(title, search, controls, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setup() {
        addColumn("Record ID", "recordId", 110);
        addColumn("Student ID", "studentId", 100);
        addColumn("Student", "studentName", 150);
        addColumn("Book ID", "bookId", 90);
        addColumn("Book", "bookTitle", 200);
        addColumn("Issue Date", "issueDate", 105);
        addColumn("Due Date", "dueDate", 105);
        addColumn("Return Date", "returnDate", 105);
        addColumn("Fine", "fine", 80);
        addColumn("Fine Status", "fineStatus", 100);
        addColumn("Status", "status", 100);
    }

    private void addColumn(String title, String property, int width) {
        TableColumn<BorrowRecord, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void refresh(LibraryService service) {
        activeLoans.setAll(service.activeLoans());
    }

    private void refreshHistory(LibraryService service) {
        table.setItems(FXCollections.observableArrayList(service.getHistory()));
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
