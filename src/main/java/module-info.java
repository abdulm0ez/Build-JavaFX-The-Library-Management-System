module com.example.thelibrarymanagementsystem {
    requires javafx.controls;

    exports com.example.thelibrarymanagementsystem;
    exports com.example.thelibrarymanagementsystem.model;
    exports com.example.thelibrarymanagementsystem.service;
    exports com.example.thelibrarymanagementsystem.ui;
    exports com.example.thelibrarymanagementsystem.util;

    opens com.example.thelibrarymanagementsystem.model to javafx.base;
    opens com.example.thelibrarymanagementsystem.ui to javafx.base;
}
