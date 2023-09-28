package com.khan.tickets.demo.model;

public enum TicketStatus {
    UNASSIGNED(0), OPEN(1), CLOSED(2);

    private int id;

    TicketStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
