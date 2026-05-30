package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.stream.Collectors;

public class DashboardView extends VBox {
    public DashboardView(LibraryService service) {
        setSpacing(20);
        setPadding(new Insets(26));

        Label title = new Label("Dashboard");
        title.getStyleClass().add("page-title");

        HBox cards = new HBox(14,
                card("Total Books", service.getBooks().stream().mapToInt(Book::getTotalCopies).sum()),
                card("Issued Books", service.issuedBooksCount()),
                card("Available Books", service.availableBooksCount()),
                card("Total Students", service.getStudents().size()),
                card("Unpaid Fine", (int) service.unpaidFineTotal()));

        PieChart pie = new PieChart(FXCollections.observableArrayList(
                new PieChart.Data("Available", service.availableBooksCount()),
                new PieChart.Data("Issued", service.issuedBooksCount())));
        pie.setTitle("Copy Status");
        pie.getStyleClass().add("chart-card");

        CategoryAxis x = new CategoryAxis();
        NumberAxis y = new NumberAxis();
        BarChart<String, Number> bar = new BarChart<>(x, y);
        bar.setTitle("Books by Category");
        bar.setLegendVisible(false);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<String, Long> byCategory = service.getBooks().stream()
                .collect(Collectors.groupingBy(Book::getCategory, Collectors.counting()));
        byCategory.forEach((category, count) -> series.getData().add(new XYChart.Data<>(category, count)));
        bar.getData().add(series);

        GridPane charts = new GridPane();
        charts.setHgap(18);
        charts.add(pie, 0, 0);
        charts.add(bar, 1, 0);
        GridPane.setHgrow(pie, Priority.ALWAYS);
        GridPane.setHgrow(bar, Priority.ALWAYS);

        getChildren().addAll(title, cards, charts);
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
