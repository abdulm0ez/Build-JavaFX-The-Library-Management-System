package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class UserManagementView extends VBox {
    private final TableView<User> table = new TableView<>();

    public UserManagementView(LibraryService service) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("User Management");
        title.getStyleClass().add("page-title");
        add("Username", user -> user.username(), 140);
        add("Role", user -> user.role(), 100);
        add("Name", user -> user.displayName(), 180);
        add("Student ID", user -> user.studentId(), 110);
        add("Approved", user -> user.approved(), 90);
        table.setItems(service.getUsers());

        Button approve = new Button("Approve");
        Button block = new Button("Block");
        approve.getStyleClass().add("primary-button");
        block.getStyleClass().add("danger-button");
        approve.setOnAction(event -> {
            service.approveUser(table.getSelectionModel().getSelectedItem(), true);
            table.refresh();
        });
        block.setOnAction(event -> {
            service.approveUser(table.getSelectionModel().getSelectedItem(), false);
            table.refresh();
        });
        getChildren().addAll(title, new HBox(10, approve, block), table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void add(String title, java.util.function.Function<User, Object> mapper, int width) {
        TableColumn<User, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleObjectProperty<>(mapper.apply(data.getValue())));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
