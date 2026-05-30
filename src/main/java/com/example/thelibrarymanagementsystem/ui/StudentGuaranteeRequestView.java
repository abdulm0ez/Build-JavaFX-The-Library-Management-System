package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.SecurityDeposit;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDate;

public class StudentGuaranteeRequestView extends VBox {
    private final TableView<SecurityDeposit> table = new TableView<>();

    public StudentGuaranteeRequestView(LibraryService service, User user) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Guarantee Request");
        title.getStyleClass().add("page-title");
        Student student = service.studentForUser(user).orElse(null);

        Spinner<Double> amount = new Spinner<>(100.0, 100000.0, 1000.0, 100.0);
        amount.setEditable(true);
        DatePicker date = new DatePicker(LocalDate.now());
        Button request = new Button("Request Guarantee");
        request.getStyleClass().add("primary-button");

        GridPane form = new GridPane();
        form.getStyleClass().add("form-panel");
        form.setPadding(new Insets(16));
        form.setHgap(12);
        form.setVgap(12);
        form.addRow(0, new Label("Guarantee Amount"), amount);
        form.addRow(1, new Label("Request Date"), date);
        form.add(request, 1, 2);

        setupTable();
        refresh(service, student);
        request.setOnAction(event -> {
            try {
                if (student == null) throw new IllegalArgumentException("No student profile is linked to this account.");
                service.submitDeposit(student, amount.getValue(), date.getValue(), student.getName());
                refresh(service, student);
                Alerts.info("Guarantee Requested", "Your guarantee request was sent to admin for verification.");
            } catch (RuntimeException ex) {
                Alerts.error("Request Failed", ex.getMessage());
            }
        });

        getChildren().addAll(title, new Label("Admin must verify your guarantee before you can request books."), form, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setupTable() {
        add("Guarantee ID", "depositId", 120);
        add("Amount", "depositAmount", 100);
        add("Date", "depositDate", 110);
        add("Status", "depositStatus", 120);
        add("Refund", "refundStatus", 140);
    }

    private void add(String title, String property, int width) {
        TableColumn<SecurityDeposit, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void refresh(LibraryService service, Student student) {
        table.setItems(FXCollections.observableArrayList(student == null ? java.util.List.of()
                : service.getDeposits().stream()
                .filter(deposit -> deposit.getStudentId().equals(student.getStudentId()))
                .toList()));
    }
}
