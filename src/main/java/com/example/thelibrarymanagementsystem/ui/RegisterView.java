package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.InstitutionType;
import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView extends BorderPane {
    public RegisterView(Stage stage, LibraryService service) {
        getStyleClass().add("login-root");

        VBox panel = new VBox(16);
        panel.setMaxWidth(520);
        panel.setPadding(new Insets(34));
        panel.setAlignment(Pos.CENTER_LEFT);
        panel.getStyleClass().add("login-panel");

        Label title = new Label("Library Registration");
        title.getStyleClass().add("brand-title");
        Label subtitle = new Label("Create a student record for borrowing books");
        subtitle.getStyleClass().add("muted");

        TextField id = new TextField();
        TextField name = new TextField();
        ComboBox<InstitutionType> institutionType = new ComboBox<>();
        institutionType.getItems().addAll(InstitutionType.SCHOOL, InstitutionType.COLLEGE, InstitutionType.UNIVERSITY);
        institutionType.setValue(InstitutionType.SCHOOL);
        TextField academicGroup = new TextField();
        TextField academicLevel = new TextField();
        TextField institutionName = new TextField();
        TextField address = new TextField();
        TextField phone = new TextField();
        TextField email = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        PasswordField confirmPassword = new PasswordField();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        Label groupLabel = new Label();
        Label levelLabel = new Label();
        Label institutionNameLabel = new Label();
        Runnable updateLabels = () -> {
            InstitutionType type = institutionType.getValue();
            groupLabel.setText(type == InstitutionType.SCHOOL ? "Class" : "Department / Program");
            levelLabel.setText(type == InstitutionType.SCHOOL ? "Section" : "Semester / Year");
            institutionNameLabel.setText(type == InstitutionType.SCHOOL ? "School Name" : type == InstitutionType.COLLEGE ? "College Name" : "University Name");
        };
        institutionType.valueProperty().addListener((obs, old, value) -> updateLabels.run());
        updateLabels.run();
        grid.addRow(0, new Label("Student ID"), id);
        grid.addRow(1, new Label("Name"), name);
        grid.addRow(2, new Label("Institution Type"), institutionType);
        grid.addRow(3, groupLabel, academicGroup);
        grid.addRow(4, levelLabel, academicLevel);
        grid.addRow(5, institutionNameLabel, institutionName);
        grid.addRow(6, new Label("Address"), address);
        grid.addRow(7, new Label("Phone"), phone);
        grid.addRow(8, new Label("Email"), email);
        grid.addRow(9, new Label("Username"), username);
        grid.addRow(10, new Label("Password"), password);
        grid.addRow(11, new Label("Confirm Password"), confirmPassword);

        Button register = new Button("Submit Registration");
        register.getStyleClass().add("primary-button");
        register.setMaxWidth(Double.MAX_VALUE);
        Button back = new Button("Back to Login");
        back.getStyleClass().add("soft-button");
        back.setMaxWidth(Double.MAX_VALUE);

        register.setOnAction(event -> {
            try {
                service.registerStudentAccount(new Student(id.getText(), name.getText(), institutionType.getValue(),
                        academicGroup.getText(), academicLevel.getText(), institutionName.getText(),
                        address.getText(), phone.getText(), email.getText()),
                        username.getText(), password.getText(), confirmPassword.getText());
                Alerts.info("Registration Complete", "Registration submitted. Admin approval is required before login.");
                stage.getScene().setRoot(new LoginView(stage, service));
            } catch (RuntimeException ex) {
                Alerts.error("Registration Failed", ex.getMessage());
            }
        });
        back.setOnAction(event -> stage.getScene().setRoot(new LoginView(stage, service)));

        panel.getChildren().addAll(title, subtitle, grid, register, back);
        setCenter(panel);
    }
}
