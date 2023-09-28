package com.khan.tickets.demo.repository;

import com.khan.tickets.demo.model.Assignment;
import com.khan.tickets.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    long countByAssignee(User user);
}
