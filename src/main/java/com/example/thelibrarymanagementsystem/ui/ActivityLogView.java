package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.ActivityLog;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActivityLogView extends VBox {
    public ActivityLogView(LibraryService service) {
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Activity Log");
        title.getStyleClass().add("page-title");
        TableView<ActivityLog> table = new TableView<>(service.getActivityLogs());
        add(table, "Time", "timestamp", 190);
        add(table, "Actor", "actor", 150);
        add(table, "Action", "action", 160);
        add(table, "Details", "details", 360);
        getChildren().addAll(title, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void add(TableView<ActivityLog> table, String title, String property, int width) {
        TableColumn<ActivityLog, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }
}
