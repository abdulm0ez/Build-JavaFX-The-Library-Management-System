package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Role;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainLayout extends BorderPane {
    private final Stage stage;
    private final LibraryService service;
    private final User user;
    private final VBox nav = new VBox(8);

    public MainLayout(Stage stage, LibraryService service, User user) {
        this.stage = stage;
        this.service = service;
        this.user = user;
        getStyleClass().add("app-root");
        setLeft(sidebar());
        show(switch (user.role()) {
            case ADMIN -> "Dashboard";
            case LIBRARIAN -> "Daily Summary";
            case STUDENT -> "My Profile";
        });
    }

    private VBox sidebar() {
        nav.getStyleClass().add("sidebar");
        nav.setPrefWidth(230);
        nav.setPadding(new Insets(22));

        Label title = new Label("Knowledge Hub");
        title.getStyleClass().add("sidebar-title");
        Label role = new Label(user.displayName() + " | " + user.role());
        role.getStyleClass().add("sidebar-role");

        nav.getChildren().addAll(title, role);
        if (user.role() == Role.ADMIN) {
            nav.getChildren().addAll(item("Dashboard"), item("Books"), item("Students"), item("Reports"),
                    item("Deposits"), item("Fine Payments"), item("Users"), item("Activity Log"));
        } else if (user.role() == Role.LIBRARIAN) {
            nav.getChildren().addAll(item("Daily Summary"), item("Book Requests"), item("Issue Book"), item("Return Book"),
                    item("Books"), item("Students"), item("Overdue Books"), item("Fine Payments"));
        } else {
            nav.getChildren().addAll(item("My Profile"), item("Guarantee Request"), item("Available Books"), item("Issue Book"),
                    item("Return Book"), item("Overdue Notifications"));
        }

        Button logout = item("Logout");
        logout.setOnAction(event -> stage.getScene().setRoot(new LoginView(stage, service)));
        VBox.setMargin(logout, new Insets(24, 0, 0, 0));
        nav.getChildren().add(logout);
        return nav;
    }

    private Button item(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        if (!text.equals("Logout")) {
            button.setOnAction(event -> show(text));
        }
        return button;
    }

    private void show(String page) {
        for (var node : nav.getChildren()) {
            node.getStyleClass().remove("nav-active");
            if (node instanceof Button button && button.getText().equals(page)) {
                button.getStyleClass().add("nav-active");
            }
        }

        setCenter(switch (page) {
            case "Books" -> new BookTableView(service, user);
            case "Students" -> new StudentView(service, user);
            case "Issue Book" -> user.role() == Role.STUDENT
                    ? new StudentIssueBookView(service, user)
                    : new IssueBookView(service, user);
            case "Return Book" -> user.role() == Role.STUDENT
                    ? new StudentReturnBookView(service, user)
                    : new ReturnBookView(service, user);
            case "Book Requests" -> new BookRequestsView(service, user.displayName());
            case "Guarantee Request" -> new StudentGuaranteeRequestView(service, user);
            case "Available Books" -> new StudentAvailableBooksView(service, user);
            case "Overdue Notifications" -> new StudentOverdueView(service, user);
            case "History" -> new HistoryView(service);
            case "Reports" -> new ReportsView(service);
            case "Deposits" -> new DepositView(service, user);
            case "Fine Payments" -> new FinePaymentView(service, user);
            case "Overdue Books" -> new OverdueBooksView(service);
            case "Daily Summary" -> new LibrarianDashboardView(service);
            case "Users" -> new UserManagementView(service);
            case "Activity Log" -> new ActivityLogView(service);
            case "My Profile" -> new StudentProfileView(service, user);
            default -> new DashboardView(service);
        });
    }
}
