package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.model.InstitutionType;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StudentProfileView extends VBox {
    private final LibraryService service;
    private final Student student;
    private final TableView<BorrowRecord> history = new TableView<>();

    public StudentProfileView(LibraryService service, User user) {
        this(service, service.studentForUser(user).orElse(null));
    }

    public StudentProfileView(LibraryService service, Student student) {
        this.service = service;
        this.student = student;
        setSpacing(16);
        setPadding(new Insets(26));

        Label title = new Label("Student Profile");
        title.getStyleClass().add("page-title");
        if (student == null) {
            getChildren().addAll(title, new Label("No student profile is linked to this account."));
            return;
        }

        setupBorrowTable(history);
        history.setPlaceholder(new Label("No borrow history yet."));
        Label historyTitle = new Label("Borrow History");
        historyTitle.getStyleClass().add("section-title");

        getChildren().addAll(title, details(), summary(), depositStatus(), historyTitle, history);
        VBox.setVgrow(history, Priority.ALWAYS);
        refreshTables();
    }

    private GridPane details() {
        GridPane details = new GridPane();
        details.getStyleClass().add("form-panel");
        details.setPadding(new Insets(16));
        details.setHgap(14);
        details.setVgap(10);
        details.addRow(0, new Label("Student ID"), new Label(student.getStudentId()));
        details.addRow(1, new Label("Name"), new Label(student.getName()));
        details.addRow(2, new Label("Institution Type"), new Label(student.getInstitute()));
        details.addRow(3, new Label(student.getInstitutionType() == InstitutionType.SCHOOL ? "Class" : "Department / Program"), new Label(student.getAcademicGroup()));
        details.addRow(4, new Label(student.getInstitutionType() == InstitutionType.SCHOOL ? "Section" : "Semester / Year"), new Label(student.getAcademicLevel()));
        details.addRow(5, new Label(student.getInstitutionType() == InstitutionType.SCHOOL ? "School Name" : student.getInstitutionType() == InstitutionType.COLLEGE ? "College Name" : "University Name"), new Label(student.getInstitutionName()));
        details.addRow(6, new Label("Address"), new Label(student.getAddress()));
        details.addRow(7, new Label("Phone"), new Label(student.getPhone()));
        details.addRow(8, new Label("Email"), new Label(student.getEmail()));
        return details;
    }

    private Label summary() {
        double totalFine = service.historyForStudent(student.getStudentId()).stream()
                .mapToDouble(BorrowRecord::getFine)
                .sum();
        Label summary = new Label("Borrowed: " + service.historyForStudent(student.getStudentId()).size()
                + "    Active: " + service.activeLoansForStudent(student.getStudentId()).size()
                + "    Total Fine: Rs. " + totalFine);
        summary.getStyleClass().add("section-title");
        return summary;
    }

    private Label depositStatus() {
        Label label = new Label(service.hasActiveDeposit(student.getStudentId())
                ? "Guarantee Deposit: VERIFIED"
                : "Guarantee Deposit: REQUIRED - ask admin to submit and verify deposit before issuing a book.");
        label.getStyleClass().add(service.hasActiveDeposit(student.getStudentId()) ? "section-title" : "warning-text");
        return label;
    }

    private void setupBorrowTable(TableView<BorrowRecord> table) {
        addBorrowColumn(table, "Issue ID", "issueId", 110);
        addBorrowColumn(table, "Book ID", "bookId", 90);
        addBorrowColumn(table, "Book", "bookTitle", 220);
        addBorrowColumn(table, "Issue Date", "issueDate", 105);
        addBorrowColumn(table, "Due Date", "dueDate", 105);
        addBorrowColumn(table, "Return Date", "returnDate", 105);
        addBorrowColumn(table, "Fine", "fine", 80);
        addBorrowColumn(table, "Status", "issueStatus", 100);
    }

    private void refreshTables() {
        history.setItems(FXCollections.observableArrayList(service.historyForStudent(student.getStudentId())));
    }

    private void addBorrowColumn(TableView<BorrowRecord> table, String title, String property, int width) {
        TableColumn<BorrowRecord, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
