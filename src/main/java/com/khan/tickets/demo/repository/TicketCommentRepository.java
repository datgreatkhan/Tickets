package com.khan.tickets.demo.repository;

import com.khan.tickets.demo.model.TicketComment;
import com.khan.tickets.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {

    long countByOwner(User user);
}
