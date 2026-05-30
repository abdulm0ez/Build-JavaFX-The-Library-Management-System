package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Student;
import com.example.thelibrarymanagementsystem.model.Role;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.model.InstitutionType;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;


public class StudentView extends VBox {
    private final LibraryService service;
    private final User user;
    private final TableView<Student> table = new TableView<>();
    private final FilteredList<Student> filteredStudents;

    public StudentView(LibraryService service, User user) {
        this.service = service;
        this.user = user;
        this.filteredStudents = new FilteredList<>(service.getStudents(), student -> true);
        setSpacing(16);
        setPadding(new Insets(26));
        Label title = new Label("Student Management");
        title.getStyleClass().add("page-title");
        Button add = new Button("Add Student");
        Button edit = new Button("Update");
        Button delete = new Button("Delete");
        Button profile = new Button("Profile");
        add.getStyleClass().add("primary-button");
        edit.getStyleClass().add("soft-button");
        delete.getStyleClass().add("danger-button");
        profile.getStyleClass().add("soft-button");
        add.setOnAction(e -> form(null));
        edit.setOnAction(e -> form(table.getSelectionModel().getSelectedItem()));
        delete.setOnAction(e -> delete());
        profile.setOnAction(e -> showProfile());
        Button all = filterButton("All", null);
        Button school = filterButton("School", InstitutionType.SCHOOL);
        Button college = filterButton("College", InstitutionType.COLLEGE);
        Button university = filterButton("University", InstitutionType.UNIVERSITY);
        HBox toolbar = new HBox(10);
        if (user.role() == Role.ADMIN) {
            toolbar.getChildren().addAll(add, edit, delete);
        }
        toolbar.getChildren().addAll(profile, all, school, college, university);
        setup(null);
        getChildren().addAll(title, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setup(InstitutionType type) {
        table.getColumns().clear();
        addColumn("Student ID", "studentId", 110);
        addColumn("Name", "name", 180);
        if (type == InstitutionType.SCHOOL) {
            addColumn("Class", "studentClass", 90);
            addColumn("Section", "section", 90);
            addColumn("School Name", "schoolName", 190);
        } else if (type == InstitutionType.COLLEGE) {
            addColumn("Department / Program", "departmentProgram", 190);
            addColumn("Semester / Year", "semesterYear", 130);
            addColumn("College Name", "collegeName", 190);
        } else if (type == InstitutionType.UNIVERSITY) {
            addColumn("Department / Program", "departmentProgram", 190);
            addColumn("Semester / Year", "semesterYear", 130);
            addColumn("University Name", "universityName", 190);
        } else {
            addColumn("Institution", "institute", 110);
            addColumn("Academic Group", "academicGroup", 150);
            addColumn("Academic Level", "academicLevel", 130);
            addColumn("Institution Name", "institutionName", 190);
        }
        addColumn("Address", "address", 210);
        addColumn("Phone", "phone", 130);
        addColumn("Email", "email", 200);
        table.setItems(filteredStudents);
    }

    private Button filterButton(String label, InstitutionType institutionType) {
        Button button = new Button(label);
        button.getStyleClass().add("soft-button");
        button.setOnAction(event -> {
            filteredStudents.setPredicate(student ->
                    institutionType == null || student.getInstitutionType() == institutionType);
            setup(institutionType);
        });
        return button;
    }

    private void addColumn(String title, String property, int width) {
        TableColumn<Student, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void delete() {
        Student student = table.getSelectionModel().getSelectedItem();
        if (student == null) {
            Alerts.error("No Student Selected", "Select a student first.");
            return;
        }
        if (Alerts.confirm("Delete Student", "Delete " + student.getName() + "?")) {
            try {
                service.deleteStudent(student);
                table.refresh();
            } catch (IllegalArgumentException ex) {
                Alerts.error("Delete Failed", ex.getMessage());
            }
        }
    }

    private void form(Student existing) {
        Dialog<Student> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Student" : "Update Student");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField id = new TextField(existing == null ? "" : existing.getStudentId());
        id.setDisable(existing != null);
        TextField name = new TextField(existing == null ? "" : existing.getName());
        ComboBox<InstitutionType> institutionType = new ComboBox<>();
        institutionType.getItems().addAll(InstitutionType.SCHOOL, InstitutionType.COLLEGE, InstitutionType.UNIVERSITY);
        institutionType.setValue(existing == null ? InstitutionType.SCHOOL : existing.getInstitutionType());
        TextField academicGroup = new TextField(existing == null ? "" : existing.getAcademicGroup());
        TextField academicLevel = new TextField(existing == null ? "" : existing.getAcademicLevel());
        TextField institutionName = new TextField(existing == null ? "" : existing.getInstitutionName());
        TextField address = new TextField(existing == null ? "" : existing.getAddress());
        TextField phone = new TextField(existing == null ? "" : existing.getPhone());
        TextField email = new TextField(existing == null ? "" : existing.getEmail());
        Label groupLabel = new Label();
        Label levelLabel = new Label();
        Label nameLabel = new Label();
        Runnable updateLabels = () -> {
            InstitutionType type = institutionType.getValue();
            groupLabel.setText(type == InstitutionType.SCHOOL ? "Class" : "Department / Program");
            levelLabel.setText(type == InstitutionType.SCHOOL ? "Section" : "Semester / Year");
            nameLabel.setText(type == InstitutionType.SCHOOL ? "School Name" : type == InstitutionType.COLLEGE ? "College Name" : "University Name");
        };
        institutionType.valueProperty().addListener((obs, old, value) -> updateLabels.run());
        updateLabels.run();
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        grid.addRow(0, new Label("Student ID"), id);
        grid.addRow(1, new Label("Name"), name);
        grid.addRow(2, new Label("Institution Type"), institutionType);
        grid.addRow(3, groupLabel, academicGroup);
        grid.addRow(4, levelLabel, academicLevel);
        grid.addRow(5, nameLabel, institutionName);
        grid.addRow(6, new Label("Address"), address);
        grid.addRow(7, new Label("Phone"), phone);
        grid.addRow(8, new Label("Email"), email);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Student(id.getText(), name.getText(), institutionType.getValue(), academicGroup.getText(),
                academicLevel.getText(), institutionName.getText(), address.getText(), phone.getText(), email.getText())
                : null);
        dialog.showAndWait().ifPresent(student -> {
            try {
                if (existing == null && service.studentIdExists(student.getStudentId())) {
                    throw new IllegalArgumentException("Duplicate student ID already exists.");
                }
                service.upsertStudent(student, user.displayName());
                table.refresh();
                Alerts.info("Saved", "Student saved successfully.");
            } catch (RuntimeException ex) {
                Alerts.error("Save Failed", ex.getMessage());
            }
        });
    }

    private void showProfile() {
        Student student = table.getSelectionModel().getSelectedItem();
        if (student == null) {
            Alerts.error("No Student Selected", "Select a student first.");
            return;
        }
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Student Profile");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setContent(new StudentProfileView(service, student));
        dialog.getDialogPane().setPrefSize(820, 560);
        dialog.showAndWait();
    }
}
