package com.g3.sppts.model;

import java.io.Serializable;

/** A registered user: either a student or a faculty member. */
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String email;
    private String role; // "student" or "faculty"

    public User() { }

    public User(int id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isFaculty() { return "faculty".equals(role); }
    public boolean isStudent() { return "student".equals(role); }
}
