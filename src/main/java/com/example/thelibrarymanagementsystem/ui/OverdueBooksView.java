package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class OverdueBooksView extends VBox {
    public OverdueBooksView(LibraryService service) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Overdue Books");
        title.getStyleClass().add("page-title");
        TableView<OverdueRow> table = new TableView<>();
        add(table, "Record ID", "recordId", 110);
        add(table, "Student", "studentName", 160);
        add(table, "Book", "bookTitle", 220);
        add(table, "Due Date", "dueDate", 110);
        add(table, "Late Days", "lateDays", 90);
        add(table, "Estimated Fine", "estimatedFine", 120);
        table.setItems(FXCollections.observableArrayList(service.overdueLoans().stream()
                .map(r -> new OverdueRow(r, service.estimateFine(r, LocalDate.now())))
                .toList()));
        getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void add(TableView<OverdueRow> table, String title, String property, int width) {
        TableColumn<OverdueRow, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    public static class OverdueRow {
        private final String recordId;
        private final String studentName;
        private final String bookTitle;
        private final LocalDate dueDate;
        private final long lateDays;
        private final double estimatedFine;

        public OverdueRow(BorrowRecord record, double estimatedFine) {
            this.recordId = record.getRecordId();
            this.studentName = record.getStudentName();
            this.bookTitle = record.getBookTitle();
            this.dueDate = record.getDueDate();
            this.lateDays = ChronoUnit.DAYS.between(record.getDueDate(), LocalDate.now());
            this.estimatedFine = estimatedFine;
        }

        public String getRecordId() { return recordId; }
        public String getStudentName() { return studentName; }
        public String getBookTitle() { return bookTitle; }
        public LocalDate getDueDate() { return dueDate; }
        public long getLateDays() { return lateDays; }
        public double getEstimatedFine() { return estimatedFine; }
    }
}
