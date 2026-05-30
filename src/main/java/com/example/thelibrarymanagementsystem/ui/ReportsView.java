package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.BorrowRecord;
import com.example.thelibrarymanagementsystem.model.SecurityDeposit;
import com.example.thelibrarymanagementsystem.model.Student;
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

public class ReportsView extends VBox {
    private final LibraryService service;
    private final VBox tableHolder = new VBox();

    public ReportsView(LibraryService service) {
        this.service = service;
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Reports");
        title.getStyleClass().add("page-title");

        Button issued = new Button("Issue Book Report");
        Button returned = new Button("Returned Books");
        Button overdue = new Button("Overdue Books");
        Button fines = new Button("Fine Report");
        Button deposits = new Button("Deposit Report");
        Button refunds = new Button("Refund Report");
        Button students = new Button("Student Activity");
        issued.getStyleClass().add("soft-button");
        returned.getStyleClass().add("soft-button");
        overdue.getStyleClass().add("soft-button");
        fines.getStyleClass().add("soft-button");
        deposits.getStyleClass().add("soft-button");
        refunds.getStyleClass().add("soft-button");
        students.getStyleClass().add("soft-button");
        issued.setOnAction(e -> showBorrowReport("Issued Books", service.activeLoans()));
        returned.setOnAction(e -> showBorrowReport("Returned Books", service.getHistory().stream()
                .filter(r -> r.getReturnDate() != null).toList()));
        overdue.setOnAction(e -> showBorrowReport("Overdue Books", service.overdueLoans()));
        fines.setOnAction(e -> showFineReport());
        deposits.setOnAction(e -> showDepositReport("Deposit Report", service.getDeposits()));
        refunds.setOnAction(e -> showDepositReport("Refund Report", service.getDeposits().stream()
                .filter(d -> !d.getRefundStatus().name().equals("NOT_REFUNDED")).toList()));
        students.setOnAction(e -> showStudentActivity());

        HBox rowOne = new HBox(10, issued, returned, overdue, fines);
        HBox rowTwo = new HBox(10, deposits, refunds, students);
        getChildren().addAll(title, rowOne, rowTwo, tableHolder);
        VBox.setVgrow(tableHolder, Priority.ALWAYS);
        showBorrowReport("Issued Books", service.activeLoans());
    }

    private void showBorrowReport(String title, java.util.List<BorrowRecord> records) {
        Label reportTitle = new Label(title);
        reportTitle.getStyleClass().add("section-title");
        TableView<BorrowRecord> table = borrowTable();
        table.setItems(FXCollections.observableArrayList(records));
        tableHolder.getChildren().setAll(reportTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void showFineReport() {
        showBorrowReport("Fine Report", service.getHistory().stream().filter(r -> r.getFine() > 0).toList());
        Alerts.info("Fine Summary", "Total unpaid fine: Rs. " + service.unpaidFineTotal());
    }

    private void showDepositReport(String title, java.util.List<SecurityDeposit> records) {
        Label reportTitle = new Label(title);
        reportTitle.getStyleClass().add("section-title");
        TableView<SecurityDeposit> table = new TableView<>();
        add(table, "Deposit ID", "depositId", 120);
        add(table, "Student ID", "studentId", 110);
        add(table, "Amount", "depositAmount", 100);
        add(table, "Deposit Date", "depositDate", 120);
        add(table, "Deposit Status", "depositStatus", 130);
        add(table, "Refund Status", "refundStatus", 150);
        table.setItems(FXCollections.observableArrayList(records));
        tableHolder.getChildren().setAll(reportTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void showStudentActivity() {
        Label reportTitle = new Label("Student Activity");
        reportTitle.getStyleClass().add("section-title");
        TableView<StudentActivity> table = new TableView<>();
        add(table, "Student ID", "studentId", 110);
        add(table, "Name", "name", 170);
        add(table, "Institution Type", "institute", 120);
        add(table, "Academic Group", "academicGroup", 150);
        add(table, "Academic Level", "academicLevel", 130);
        add(table, "Institution Name", "institutionName", 180);
        add(table, "Borrowed", "borrowed", 90);
        add(table, "Active Loans", "activeLoans", 100);
        add(table, "Total Fine", "totalFine", 100);
        table.setItems(FXCollections.observableArrayList(service.getStudents().stream().map(this::activityFor).toList()));
        tableHolder.getChildren().setAll(reportTitle, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private StudentActivity activityFor(Student student) {
        long borrowed = service.getHistory().stream().filter(r -> r.getStudentId().equals(student.getStudentId())).count();
        long active = service.getHistory().stream()
                .filter(r -> r.getStudentId().equals(student.getStudentId()) && r.getReturnDate() == null)
                .count();
        double fine = service.getHistory().stream()
                .filter(r -> r.getStudentId().equals(student.getStudentId()))
                .mapToDouble(BorrowRecord::getFine)
                .sum();
        return new StudentActivity(student.getStudentId(), student.getName(), student.getInstitute(),
                student.getAcademicGroup(), student.getAcademicLevel(), student.getInstitutionName(), borrowed, active, fine);
    }

    private TableView<BorrowRecord> borrowTable() {
        TableView<BorrowRecord> table = new TableView<>();
        add(table, "Issue ID", "issueId", 110);
        add(table, "Student ID", "studentId", 100);
        add(table, "Student", "studentName", 150);
        add(table, "Book ID", "bookId", 90);
        add(table, "Book", "bookTitle", 200);
        add(table, "Admin ID", "adminId", 110);
        add(table, "Admin", "adminName", 140);
        add(table, "Deposit ID", "depositId", 120);
        add(table, "Issue Date", "issueDate", 105);
        add(table, "Due Date", "dueDate", 105);
        add(table, "Return Date", "returnDate", 105);
        add(table, "Fine Amount", "fine", 90);
        add(table, "Fine Status", "fineStatus", 100);
        add(table, "Issue Status", "issueStatus", 100);
        return table;
    }

    private <T> void add(TableView<T> table, String title, String property, int width) {
        TableColumn<T, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    public static class StudentActivity {
        private final String studentId;
        private final String name;
        private final String institute;
        private final String academicGroup;
        private final String academicLevel;
        private final String institutionName;
        private final long borrowed;
        private final long activeLoans;
        private final double totalFine;

        public StudentActivity(String studentId, String name, String institute, String academicGroup,
                               String academicLevel, String institutionName,
                               long borrowed, long activeLoans, double totalFine) {
            this.studentId = studentId;
            this.name = name;
            this.institute = institute;
            this.academicGroup = academicGroup;
            this.academicLevel = academicLevel;
            this.institutionName = institutionName;
            this.borrowed = borrowed;
            this.activeLoans = activeLoans;
            this.totalFine = totalFine;
        }

        public String getStudentId() { return studentId; }
        public String getName() { return name; }
        public String getInstitute() { return institute; }
        public String getAcademicGroup() { return academicGroup; }
        public String getAcademicLevel() { return academicLevel; }
        public String getInstitutionName() { return institutionName; }
        public long getBorrowed() { return borrowed; }
        public long getActiveLoans() { return activeLoans; }
        public double getTotalFine() { return totalFine; }
    }
}
