package com.khan.tickets.demo.model;

public enum UserRole {
    USER(0), SUPPORT(1), ADMIN(2);

    private int id;

    UserRole(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
