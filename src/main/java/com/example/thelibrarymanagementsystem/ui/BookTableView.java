package com.example.thelibrarymanagementsystem.ui;

import com.example.thelibrarymanagementsystem.model.Book;
import com.example.thelibrarymanagementsystem.model.Role;
import com.example.thelibrarymanagementsystem.model.User;
import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.util.Alerts;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class BookTableView extends VBox {
    private final LibraryService service;
    private final User user;
    private final TableView<Book> table = new TableView<>();
    private final TextField search = new TextField();
    private final ListView<String> suggestions = new ListView<>();

    public BookTableView(LibraryService service, User user) {
        this.service = service;
        this.user = user;
        setSpacing(16);
        setPadding(new Insets(26));

        Label title = new Label("Book Management");
        title.getStyleClass().add("page-title");
        search.setPromptText("Search title, author, ISBN, category or book ID");
        search.textProperty().addListener((obs, old, value) -> {
            refresh();
            suggestions.setItems(FXCollections.observableArrayList(service.bookSuggestions(value)));
            suggestions.setVisible(!suggestions.getItems().isEmpty());
        });
        suggestions.setMaxHeight(120);
        suggestions.setVisible(false);
        suggestions.managedProperty().bind(suggestions.visibleProperty());
        suggestions.setOnMouseClicked(event -> {
            String value = suggestions.getSelectionModel().getSelectedItem();
            if (value != null) {
                search.setText(value);
                suggestions.setVisible(false);
            }
        });

        Button add = new Button("Add Book");
        Button edit = new Button("Edit Selected");
        Button delete = new Button("Delete");
        add.getStyleClass().add("primary-button");
        edit.getStyleClass().add("soft-button");
        delete.getStyleClass().add("danger-button");
        add.setOnAction(e -> showForm(null));
        edit.setOnAction(e -> showForm(table.getSelectionModel().getSelectedItem()));
        delete.setOnAction(e -> deleteSelected());

        HBox toolbar = new HBox(10, search);
        if (user.role() == Role.ADMIN) {
            toolbar.getChildren().addAll(add, edit, delete);
        }
        HBox.setHgrow(search, Priority.ALWAYS);
        setupTable();
        refresh();
        Label note = new Label(user.role() == Role.LIBRARIAN ? "View-only availability search for librarian desk." : "");
        note.getStyleClass().add("muted");
        getChildren().addAll(title, toolbar, suggestions, note, table);
        VBox.setVgrow(table, Priority.ALWAYS);
    }

    private void setupTable() {
        table.getStyleClass().add("data-table");
        addColumn("Book ID", "bookId", 90);
        addColumn("ISBN", "isbn", 120);
        addColumn("Title", "title", 190);
        addColumn("Author", "author", 150);
        addColumn("Category", "category", 120);
        addColumn("Shelf", "shelfNo", 70);
        addColumn("Rack", "rackNo", 70);
        addColumn("Total", "totalCopies", 70);
        addColumn("Available", "availableCopies", 90);
        addColumn("Status", "status", 100);
    }

    private void addColumn(String title, String property, int width) {
        TableColumn<Book, Object> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        table.getColumns().add(column);
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(service.searchBooks(search.getText())));
        table.refresh();
    }

    private void deleteSelected() {
        Book book = table.getSelectionModel().getSelectedItem();
        if (book == null) {
            Alerts.error("No Book Selected", "Select a book first.");
            return;
        }
        if (Alerts.confirm("Delete Book", "Delete " + book.getTitle() + "?")) {
            try {
                service.deleteBook(book);
                refresh();
                Alerts.info("Deleted", "Book deleted successfully.");
            } catch (IllegalArgumentException ex) {
                Alerts.error("Delete Failed", ex.getMessage());
            }
        }
    }

    private void showForm(Book existing) {
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Book" : "Edit Book");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField id = field(existing == null ? "" : existing.getBookId());
        id.setDisable(existing != null);
        TextField isbn = field(existing == null ? "" : existing.getIsbn());
        TextField title = field(existing == null ? "" : existing.getTitle());
        TextField author = field(existing == null ? "" : existing.getAuthor());
        TextField publisher = field(existing == null ? "" : existing.getPublisher());
        TextField category = field(existing == null ? "" : existing.getCategory());
        TextField language = field(existing == null ? "English" : existing.getLanguage());
        TextField shelf = field(existing == null ? "" : existing.getShelfNo());
        TextField rack = field(existing == null ? "" : existing.getRackNo());
        Spinner<Integer> total = new Spinner<>(0, 999, existing == null ? 1 : existing.getTotalCopies());
        Spinner<Integer> available = new Spinner<>(0, 999, existing == null ? 1 : existing.getAvailableCopies());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(12));
        String[] labels = {"Book ID", "ISBN", "Title", "Author", "Publisher", "Category", "Language", "Shelf No", "Rack No", "Total Copies", "Available Copies"};
        Control[] controls = {id, isbn, title, author, publisher, category, language, shelf, rack, total, available};
        for (int i = 0; i < labels.length; i++) {
            grid.add(new Label(labels[i]), 0, i);
            grid.add(controls[i], 1, i);
        }
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Book(id.getText(), isbn.getText(), title.getText(), author.getText(), publisher.getText(), category.getText(),
                language.getText(), shelf.getText(), rack.getText(), total.getValue(), available.getValue())
                : null);
        dialog.showAndWait().ifPresent(book -> {
            try {
                if (existing == null && service.bookIdExists(book.getBookId())) {
                    throw new IllegalArgumentException("Duplicate book ID already exists.");
                }
                service.upsertBook(book, user.displayName());
                refresh();
                Alerts.info("Saved", "Book saved successfully.");
            } catch (RuntimeException ex) {
                Alerts.error("Save Failed", ex.getMessage());
            }
        });
    }

    private TextField field(String text) {
        TextField field = new TextField(text);
        field.setPrefWidth(260);
        return field;
    }
}
