package com.khan.tickets.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
public class TicketComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "ticket_id")
    @JsonIgnore
    private Ticket ticket;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column(name = "content")
    private String content;

    @OneToOne
    @JoinColumn(name = "reply_to")
    private TicketComment replyTo;

    public TicketComment() {}

    public TicketComment(Ticket ticket, User owner, String content) {
        this.ticket = ticket;
        this.owner = owner;
        this.content = content;
    }

    public TicketComment(Ticket ticket, User owner, String content, TicketComment replyTo) {
        this.ticket = ticket;
        this.owner = owner;
        this.content = content;
        this.replyTo = replyTo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TicketComment getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(TicketComment replyTo) {
        this.replyTo = replyTo;
    }
}
