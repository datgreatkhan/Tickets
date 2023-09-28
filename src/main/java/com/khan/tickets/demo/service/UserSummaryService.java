package com.khan.tickets.demo.service;

import com.khan.tickets.demo.data.UserSummary;
import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.repository.AssignmentRepository;
import com.khan.tickets.demo.repository.TicketCommentRepository;
import com.khan.tickets.demo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserSummaryService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    public UserSummary getSummaryOf(User user) {
        Instant joinDate = user.getCreatedAt();
        Instant lastUpdated = user.getUpdatedAt();

        long totalTickets = ticketRepository.countByOwner(user);
        long totalAssignments = assignmentRepository.countByAssignee(user);

        long totalComments = commentRepository.countByOwner(user);

        return new UserSummary(joinDate, lastUpdated, totalTickets, totalAssignments, totalComments);
    }
}
