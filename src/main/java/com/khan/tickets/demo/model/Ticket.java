package com.khan.tickets.demo.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Entity
public class Ticket {

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
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type", nullable = false)
    private TicketType type;

    @Column(name = "status", nullable = false)
    private TicketStatus status = TicketStatus.OPEN;

    @Column(name = "body", nullable = false)
    private String body;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL)
    @OrderBy(value = "id ASC")
    private List<TicketComment> comments = new ArrayList<TicketComment>();

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "id ASC")
    private List<Assignment> assignees = new ArrayList<Assignment>();

    public Ticket() {}

    public Ticket(User owner, String title, TicketType type, TicketStatus status, String body) {
        this.owner = owner;
        this.title = title;
        this.type = type;
        this.status = status;
        this.body = body;
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

    private void onUpdated() {
        this.setUpdatedAt(Instant.now());
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
        this.onUpdated();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.onUpdated();
    }

    public TicketType getType() {
        return type;
    }

    public void setType(TicketType type) {
        this.type = type;
        this.onUpdated();
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
        this.onUpdated();
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        this.onUpdated();
    }

    public List<TicketComment> getComments() {
        return comments;
    }

    public void setComments(List<TicketComment> comments) {
        this.comments = comments;
    }

    public void addComment(TicketComment comment) {
        User commentOwner = comment.getOwner();

        this.comments.add(comment);

        if((commentOwner.getRole() == UserRole.SUPPORT || commentOwner.getRole() == UserRole.ADMIN) && !this.assignees.stream().anyMatch(assignment -> assignment.getAssignee().getId() == commentOwner.getId())) {
            this.assignees.add(new Assignment(this, commentOwner));

            if(this.status == TicketStatus.UNASSIGNED) {
                this.status = TicketStatus.OPEN;
            }
        }

        this.onUpdated();
    }

    public List<Assignment> getAssignees() {
        return assignees;
    }

    public void setAssignees(List<Assignment> assignees) {
        if(assignees.isEmpty()) {
            this.assignees.clear();

            if(this.status == TicketStatus.OPEN) {
                this.status = TicketStatus.UNASSIGNED;
            }
        }else{
            this.assignees.clear();

            for(Assignment assignee : assignees) {
                this.assignees.add(assignee);
            }

            if(this.status == TicketStatus.UNASSIGNED) {
                this.status = TicketStatus.OPEN;
            }
        }

        this.onUpdated();
    }

    public void addAssignee(User assignee) {
        this.assignees.add(new Assignment(this, assignee));
        this.onUpdated();
    }

    public void removeAssignee(User assignee) {
        if(hasAssignee(assignee)) {
            Iterator<Assignment> assignments = this.assignees.iterator();

            while(assignments.hasNext()) {
                Assignment ass = assignments.next();

                if(ass.getAssignee().getId() == assignee.getId()) {
                    assignments.remove();
                    this.onUpdated();

                    return;
                }
            }
        }
    }

    public boolean hasAssignee(User assignee) {
        return this.assignees.stream().anyMatch(ass -> ass.getAssignee().getId() == assignee.getId());
    }
}
