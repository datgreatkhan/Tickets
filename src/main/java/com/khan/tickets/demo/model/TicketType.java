package com.khan.tickets.demo.model;

public enum TicketType {
    ISSUE(0), INQUIRY(1), REPORT(2), OTHER(3);

    private int id;

    TicketType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
