package com.tap.model;

import java.sql.Timestamp;

/** Maps to the users table. passwordHash must never be shown in a JSP. */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private String address;
    private String role;          // "CUSTOMER" or "ADMIN"
    private boolean active;
    private Timestamp createdAt;

    public static final String ROLE_CUSTOMER = "CUSTOMER";
    public static final String ROLE_ADMIN = "ADMIN";

    public User() { }

    public User(int userId, String username, String passwordHash, String email, String phone,
                String address, String role, boolean active, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.active = active;
        this.createdAt = createdAt;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    /** Deliberately leaves out passwordHash so it can never leak into logs. */
    @Override
    public String toString() {
        return "User{userId=" + userId + ", username='" + username + "', email='" + email
                + "', role='" + role + "', active=" + active + "}";
    }
}
