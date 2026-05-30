package com.example.thelibrarymanagementsystem;

import com.example.thelibrarymanagementsystem.service.LibraryService;
import com.example.thelibrarymanagementsystem.ui.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainLibrary extends Application {
    @Override
    public void start(Stage stage) {
        LibraryService service = new LibraryService();
        Scene scene = new Scene(new LoginView(stage, service), 1180, 720);
        scene.getStylesheets().add(getClass().getResource("/com/example/thelibrarymanagementsystem/styles.css").toExternalForm());
        stage.setTitle("Campus Knowledge Hub - Library Management System");
        stage.setMinWidth(1040);
        stage.setMinHeight(650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
