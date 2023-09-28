package com.khan.tickets.demo.repository;

import com.khan.tickets.demo.model.Assignment;
import com.khan.tickets.demo.model.Ticket;
import com.khan.tickets.demo.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByOwner(User user, Pageable pageable);
    Page<Ticket> findByAssigneesAssignee(User assignee, Pageable pageable);
    Page<Ticket> findByAssigneesAssigneeIn(List<User> assignee, Pageable pageable);

    Page<Ticket> findByCommentsOwner(User owner, Pageable pageable);

    Long countByOwner(User user);
}
