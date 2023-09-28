package com.khan.tickets.demo.model;

/**
 * Enum representation of the type of ticket.
 */
public enum TicketType {
    /**
     * Represents a general issue.
     */
    ISSUE(0),
    /**
     * Represents a general question or inquiry.
     */
    INQUIRY(1),
    /**
     * Represents a report of another user or problem.
     */
    REPORT(2),
    /**
     * Represents something that doesn't fall under the other categories.
     */
    OTHER(3);

    private int id;

    TicketType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
