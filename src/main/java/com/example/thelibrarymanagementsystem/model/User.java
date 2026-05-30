package com.example.thelibrarymanagementsystem.model;

public record User(String username, String password, Role role, String displayName, String studentId, boolean approved) {
    public User(String username, String password, Role role, String displayName) {
        this(username, password, role, displayName, "", true);
    }
}
