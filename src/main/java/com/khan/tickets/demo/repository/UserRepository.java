package com.khan.tickets.demo.repository;

import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.model.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<List<User>> findByName(String name);
    Optional<User> findByEmail(String email);

    Page<User> findByRoleIn(List<UserRole> roles, Pageable pageable);
}
