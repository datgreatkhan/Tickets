package com.khan.tickets.demo.model;

/**
 * Enum representation of the status of a ticket.
 */
public enum TicketStatus {
    /**
     * Represents a ticket that has no assignment.
     */
    UNASSIGNED(0),
    /**
     * Represents a ticket that is assigned to one or more people and is open.
     */
    OPEN(1),
    /**
     * Represents a ticket that is closed.
     */
    CLOSED(2);

    private int id;

    TicketStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
