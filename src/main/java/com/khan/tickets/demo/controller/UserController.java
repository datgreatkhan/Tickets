package com.khan.tickets.demo.controller;

import com.khan.tickets.demo.model.Ticket;
import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.model.UserRole;
import com.khan.tickets.demo.repository.AssignmentRepository;
import com.khan.tickets.demo.repository.TicketCommentRepository;
import com.khan.tickets.demo.repository.TicketRepository;
import com.khan.tickets.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final List<UserRole> ADMIN_OR_SUPPORT = Arrays.asList(UserRole.ADMIN, UserRole.SUPPORT);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private TicketCommentRepository commentRepository;

    private boolean isSupportOrAdminRole(OAuth2AuthenticatedPrincipal principal) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        if(userOptional.isPresent()) {
            User user = userOptional.get();
            UserRole role = user.getRole();

            return role == UserRole.ADMIN || role == UserRole.SUPPORT;
        }

        return false;
    }

    private record UserSummary(Instant joinedAt,
                               Instant lastUpdated,
                               long totalTickets,
                               long totalAssignments,
                               long totalComments) {}

    @GetMapping("/{id}")
    public ResponseEntity<User> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Long id) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userQuery = userRepository.findById(id);

        if(userQuery.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(userQuery.get(), HttpStatus.OK);
        }
    }

    @GetMapping("/all/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<User>> allPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Integer pageNumber,
                                               @PathVariable String sortBy,
                                               @PathVariable boolean desc) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<User> requestedPage = userRepository.findAll(PageRequest.of(pageNumber - 1, 15).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/all/admin-or-support/{pageNumber}")
    public ResponseEntity<Page<User>> allAdminOrSupportPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                               @PathVariable Integer pageNumber) {
        if(!isSupportOrAdminRole(principal)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Page<User> requestedPage = userRepository.findByRoleIn(ADMIN_OR_SUPPORT, PageRequest.of(pageNumber - 1, 10).withSort(Sort.by(Sort.Direction.DESC, "id")));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ResponseEntity<UserSummary> summary(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        User user;

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }else{
            user = userOptional.get();
        }

        Instant joinDate = user.getCreatedAt();
        Instant lastUpdated = user.getUpdatedAt();

        long totalTickets = ticketRepository.countByOwner(user);
        long totalAssignments = assignmentRepository.countByAssignee(user);

        long totalComments = commentRepository.countByOwner(user);

        return new ResponseEntity<>(new UserSummary(joinDate, lastUpdated, totalTickets, totalAssignments, totalComments), HttpStatus.OK);
    }

    @GetMapping("/summary/{id}")
    public ResponseEntity<UserSummary> summaryOf(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                 @PathVariable Long id) {
        Optional<User> userOptional = userRepository.findByEmail(principal.getAttribute("email"));

        if(userOptional.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(userOptional.get().getRole() != UserRole.ADMIN) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        Optional<User> targetUser = userRepository.findById(id);

        if(targetUser.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        User user = targetUser.get();

        Instant joinDate = user.getCreatedAt();
        Instant lastUpdated = user.getUpdatedAt();

        long totalTickets = ticketRepository.countByOwner(user);
        long totalAssignments = assignmentRepository.countByAssignee(user);

        long totalComments = commentRepository.countByOwner(user);

        return new ResponseEntity<>(new UserSummary(joinDate, lastUpdated, totalTickets, totalAssignments, totalComments), HttpStatus.OK);
    }
}
