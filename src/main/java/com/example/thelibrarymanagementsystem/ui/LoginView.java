package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends BorderPane {
    public LoginView(Stage stage, LibraryService service) {
        getStyleClass().add("login-root");

        VBox panel = new VBox(16);
        panel.setMaxWidth(420);
        panel.setPadding(new Insets(34));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.getStyleClass().add("login-panel");

        Label brand = new Label("Campus Knowledge Hub");
        brand.getStyleClass().add("brand-title");
        Label subtitle = new Label("Professional Library Management System");
        subtitle.getStyleClass().add("muted");

        TextField username = new TextField();
        username.setPromptText("Username");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button login = new Button("Login");
        login.getStyleClass().add("primary-button");
        login.setMaxWidth(Double.MAX_VALUE);
        login.setDefaultButton(true);

        Button register = new Button("Register to Library");
        register.getStyleClass().add("soft-button");
        register.setMaxWidth(Double.MAX_VALUE);

        login.setOnAction(event -> {
            try {
                User user = service.login(username.getText(), password.getText())
                        .orElseThrow(() -> new IllegalArgumentException("Invalid username or password."));
                Parent app = new MainLayout(stage, service, user);
                stage.getScene().setRoot(app);
            } catch (IllegalArgumentException ex) {
                Alerts.error("Login Failed", ex.getMessage());
            }
        });

        register.setOnAction(event -> stage.getScene().setRoot(new RegisterView(stage, service)));

        panel.getChildren().addAll(brand, subtitle, username, password, login, register);
        setCenter(panel);
    }
}
