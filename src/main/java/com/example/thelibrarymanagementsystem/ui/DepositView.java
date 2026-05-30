package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.DepositStatus;
import com.example.thelibrarymanagementsystem.model.RefundStatus;
import com.example.thelibrarymanagementsystem.model.SecurityDeposit;
import com.example.thelibrarymanagementsystem.model.User;
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

public class DepositView extends VBox {
    private final LibraryService service;
    private final User user;
    private final TableView<SecurityDeposit> table = new TableView<>();

    public DepositView(LibraryService service, User user) {
        this.service = service;
        this.user = user;
        setSpacing(16);
        setPadding(new Insets(26));

        Label title = new Label("Guarantee / Security Deposit");
        title.getStyleClass().add("page-title");

        setupTable();
        table.setItems(service.getDeposits());

        Button verify = new Button("Verify");
        Button reject = new Button("Reject");
        Button refund = new Button("Refund");
        Button deduct = new Button("Deduct For Fine");
        verify.getStyleClass().add("soft-button");
        reject.getStyleClass().add("danger-button");
        refund.getStyleClass().add("soft-button");
        deduct.getStyleClass().add("soft-button");

        verify.setOnAction(event -> updateStatus(DepositStatus.VERIFIED));
        reject.setOnAction(event -> updateStatus(DepositStatus.REJECTED));
        refund.setOnAction(event -> refund(RefundStatus.REFUNDED));
        deduct.setOnAction(event -> refund(RefundStatus.DEDUCTED_FOR_FINE));

        Label note = new Label("Students submit guarantee requests from their student panel. Admin only verifies, rejects, refunds, or deducts.");
        note.getStyleClass().add("muted");
        HBox actions = new HBox(10, verify, reject, refund, deduct);
        getChildren().addAll(title, note, actions, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setupTable() {
        addColumn("Guarantee ID", "depositId", 120);
        addColumn("Student ID", "studentId", 110);
        addColumn("Guarantee Amount", "depositAmount", 130);
        addColumn("Guarantee Date", "depositDate", 120);
        addColumn("Guarantee Status", "depositStatus", 130);
        addColumn("Refund Status", "refundStatus", 150);
    }

    private void addColumn(String title, String property, int width) {
        TableColumn<SecurityDeposit, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void updateStatus(DepositStatus status) {
        try {
            service.updateDepositStatus(table.getSelectionModel().getSelectedItem(), status, user.displayName());
            table.refresh();
        } catch (RuntimeException ex) {
            Alerts.error("Status Update Failed", ex.getMessage());
        }
    }

    private void refund(RefundStatus status) {
        try {
            service.refundDeposit(table.getSelectionModel().getSelectedItem(), status, user.displayName());
            table.refresh();
            Alerts.info("Refund Updated", "Deposit refund status updated.");
        } catch (RuntimeException ex) {
            Alerts.error("Refund Failed", ex.getMessage());
        }
    }
}
