package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LibrarianDashboardView extends VBox {
    public LibrarianDashboardView(LibraryService service) {
        setSpacing(20);
        setPadding(new Insets(26));
        Label title = new Label("Daily Librarian Summary");
        title.getStyleClass().add("page-title");
        HBox cards = new HBox(14,
                card("Issued Today", (int) service.issuedTodayCount()),
                card("Returned Today", (int) service.returnedTodayCount()),
                card("Overdue Books", service.overdueLoans().size()),
                card("Fine Collected", (int) service.fineCollectedToday()),
                card("Active Loans", service.activeLoans().size()));
        Label note = new Label("Use this page at the desk before starting issue/return work.");
        note.getStyleClass().add("muted");
        getChildren().addAll(title, cards, note);
    }

    private VBox card(String label, int value) {
        Label name = new Label(label);
        name.getStyleClass().add("metric-label");
        Label number = new Label(String.valueOf(value));
        number.getStyleClass().add("metric-value");
        VBox box = new VBox(8, name, number);
        box.getStyleClass().add("metric-card");
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }
}
