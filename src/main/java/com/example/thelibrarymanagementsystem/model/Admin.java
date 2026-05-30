package com.example.thelibrarymanagementsystem.model;

public class Admin {
    private String adminId;
    private String name;
    private String phone;
    private String email;

    public Admin(String adminId, String name, String phone, String email) {
        this.adminId = adminId;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getAdminId() { return adminId; }
    public void setAdminId(String adminId) { this.adminId = adminId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
