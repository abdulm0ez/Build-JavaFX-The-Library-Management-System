package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
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

import java.time.LocalDate;

public class StudentOverdueView extends VBox {
    public StudentOverdueView(LibraryService service, User user) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Overdue Notifications");
        title.getStyleClass().add("page-title");
        Student student = service.studentForUser(user).orElse(null);
        TableView<OverdueRow> table = new TableView<>();
        add(table, "Issue ID", "issueId", 110);
        add(table, "Book", "bookTitle", 220);
        add(table, "Due Date", "dueDate", 110);
        add(table, "Estimated Fine", "estimatedFine", 120);
        table.setItems(FXCollections.observableArrayList(student == null ? java.util.List.of()
                : service.overdueLoans().stream()
                .filter(record -> record.getStudentId().equals(student.getStudentId()))
                .map(record -> new OverdueRow(record, service.estimateFine(record, LocalDate.now())))
                .toList()));
        table.setPlaceholder(new Label("No overdue books."));
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
        private final String issueId;
        private final String bookTitle;
        private final LocalDate dueDate;
        private final double estimatedFine;

        public OverdueRow(BorrowRecord record, double estimatedFine) {
            this.issueId = record.getIssueId();
            this.bookTitle = record.getBookTitle();
            this.dueDate = record.getDueDate();
            this.estimatedFine = estimatedFine;
        }

        public String getIssueId() { return issueId; }
        public String getBookTitle() { return bookTitle; }
        public LocalDate getDueDate() { return dueDate; }
        public double getEstimatedFine() { return estimatedFine; }
    }
}
