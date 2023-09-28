package com.khan.tickets.demo.controller;

import com.khan.tickets.demo.model.*;
import com.khan.tickets.demo.repository.TicketRepository;
import com.khan.tickets.demo.repository.UserRepository;
import com.khan.tickets.demo.service.TicketCreationService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.spel.ast.Assign;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RenderingResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ticket")
/**
 * Controller that deals with all Ticket related action, view or state.
 * Examples include things such as creating, updating, etc.
 */
public class TicketController {

    /*
        TODO:
            Logging of actions (assignment, reply, etc.)
            Role assignment in "View User"
            Edit ticket body & edit comments
            HTML for replies/bodies
     */

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCreationService ticketCreationService;

    private Optional<User> getUser(OAuth2AuthenticatedPrincipal principal) {
        if(principal == null) {
            return Optional.empty();
        }else{
            return userRepository.findByEmail(principal.getAttribute("email"));
        }
    }

    private boolean isAdminRole(User user) {
        return user.getRole() == UserRole.ADMIN;
    }

    private boolean isSupportOrAdminRole(User user) {
        UserRole role = user.getRole();

        return role == UserRole.ADMIN || role == UserRole.SUPPORT;
    }

    private record TicketAssignment(Long ticket, Long user) {}
    private record TicketAssignees(Long ticket, List<User> assignees) {}
    private record TicketStatusUpdate(Long ticket, TicketStatus status) {}
    private record TicketReply(Long ticket, TicketComment comment) {}
    private record TicketEdit(Long ticket, String changeTo) {}

    @GetMapping("/all")
    public ResponseEntity<List<Ticket>> all(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal) {
        Optional<User> user = getUser(principal);

        if(user.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(!isAdminRole(user.get())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        List<Ticket> tickets = ticketRepository.findAll();

        if(tickets.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(tickets, HttpStatus.OK);
        }
    }

    @GetMapping("/all/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<Ticket>> allPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                 @PathVariable Integer pageNumber,
                                                 @PathVariable String sortBy,
                                                 @PathVariable boolean desc) {
        Optional<User> user = getUser(principal);

        if(user.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(!isSupportOrAdminRole(user.get())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<Ticket> requestedPage = ticketRepository.findAll(PageRequest.of(pageNumber - 1, 15).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/all/user/{id}/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<Ticket>> allPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                 @PathVariable Long id,
                                                 @PathVariable Integer pageNumber,
                                                 @PathVariable String sortBy,
                                                 @PathVariable boolean desc) {
        Optional<User> requester = getUser(principal);

        if(requester.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(!isSupportOrAdminRole(requester.get())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Optional<User> userOptional = userRepository.findById(id);

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        User user = userOptional.get();

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<Ticket> requestedPage = ticketRepository.findByOwner(user, PageRequest.of(pageNumber - 1, 15).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/user/all/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<Ticket>> allUserPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                     @PathVariable Integer pageNumber,
                                                     @PathVariable String sortBy,
                                                     @PathVariable boolean desc) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        User owner;

        if(user.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }else{
            owner = user.get();
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<Ticket> requestedPage = ticketRepository.findByOwner(owner, PageRequest.of(pageNumber - 1, 10).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/user/assigned/{pageNumber}/{sortBy}/{desc}")
    public ResponseEntity<Page<Ticket>> allAssignedPaged(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                     @PathVariable Integer pageNumber,
                                                     @PathVariable String sortBy,
                                                     @PathVariable boolean desc) {
        Optional<User> userOptional = getUser(principal);

        if(userOptional.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        User owner = userOptional.get();

        if(!isSupportOrAdminRole(owner)) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        if(pageNumber < 1) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }

        Sort.Direction direction = desc ? Sort.Direction.DESC : Sort.Direction.ASC;

        Page<Ticket> requestedPage = ticketRepository.findByAssigneesAssignee(owner, PageRequest.of(pageNumber - 1, 10).withSort(Sort.by(direction, sortBy)));

        return new ResponseEntity<>(requestedPage, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Ticket> allCreated(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                             @PathVariable Long id) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        User owner;

        if(user.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }else{
            owner = user.get();
        }

        Optional<Ticket> ticketQuery = ticketRepository.findById(id);

        Ticket ticket;

        if(ticketQuery.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else{
            ticket = ticketQuery.get();

            if(ticket.getOwner().getId() != owner.getId()) {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }else{
                return new ResponseEntity<>(ticket, HttpStatus.OK);
            }
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ticket> get(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                      @PathVariable Long id) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        if(user.isEmpty()) { return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED); }

        if(!isSupportOrAdminRole(user.get())) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        }

        Optional<Ticket> ticketQuery = ticketRepository.findById(id);

        if(ticketQuery.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }else{
            return new ResponseEntity<>(ticketQuery.get(), HttpStatus.OK);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                @RequestBody Ticket ticketData) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        User owner;

        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            owner = user.get();
        }

        if(ticketCreationService.createTicket(owner, ticketData)) {
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/update-assignees")
    public ResponseEntity<HttpStatus> updateAssignees(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                      @RequestBody TicketAssignees ticketAssignees) {
        Optional<User> userOptional = getUser(principal);

        if(userOptional.isEmpty()) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); }

        if(!isSupportOrAdminRole(userOptional.get())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketAssignees.ticket());

        Ticket ticket;

        if(ticketOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            ticket = ticketOptional.get();
        }

        ArrayList<Assignment> assignees =
                ticketAssignees.assignees().stream()
                        .map(user -> new Assignment(ticket, user)).collect(Collectors.toCollection(ArrayList::new));

        ticket.setAssignees(assignees);

        ticketRepository.save(ticket);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/assign")
    public ResponseEntity<HttpStatus> assignUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                         @RequestBody TicketAssignment ticketAssignment) {
        Optional<User> userOptional = getUser(principal);

        if(userOptional.isEmpty()) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); }

        // TODO Add assigner in Assignment to track who assigned who
        User assigner = userOptional.get();

        Optional<User> user2 = userRepository.findById(ticketAssignment.user());

        User assignee;

        if(user2.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            assignee = user2.get();
        }

        Optional<Ticket> ticket = ticketRepository.findById(ticketAssignment.ticket());

        Ticket ticketToAssign;

        if(ticket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            ticketToAssign = ticket.get();
        }

        if(ticketToAssign.getStatus() == TicketStatus.CLOSED) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        if(ticketToAssign.hasAssignee(assignee)) { return new ResponseEntity<>(HttpStatus.CONFLICT); }

        ticketToAssign.addAssignee(assignee);

        ticketRepository.save(ticketToAssign);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/unassign")
    public ResponseEntity<HttpStatus> unassignUser(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                 @RequestBody TicketAssignment ticketAssignment) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        //User assigner;

        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }/*else{
            assigner = user.get();
        }*/

        Optional<User> user2 = userRepository.findById(ticketAssignment.user());

        User assignee;

        if(user2.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            assignee = user2.get();
        }

        Optional<Ticket> ticket = ticketRepository.findById(ticketAssignment.ticket());

        Ticket ticketToUnassign;

        if(ticket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            ticketToUnassign = ticket.get();
        }

        if(ticketToUnassign.getStatus() == TicketStatus.CLOSED) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        if(!ticketToUnassign.hasAssignee(assignee)) { return new ResponseEntity<>(HttpStatus.CONFLICT); }

        ticketToUnassign.removeAssignee(assignee);

        ticketRepository.save(ticketToUnassign);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reply")
    public ResponseEntity<HttpStatus> reply(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                    @RequestBody TicketReply ticketReply) {
        Optional<User> user = userRepository.findByEmail(principal.getAttribute("email"));

        User owner;

        if(user.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }else{
            owner = user.get();
        }

        Optional<Ticket> ticket = ticketRepository.findById(ticketReply.ticket());

        if(ticket.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ticket updatedTicket = ticket.get();

        boolean isOwnerAdminOrSupport = (owner.getRole() == UserRole.ADMIN || owner.getRole() == UserRole.SUPPORT);

        if(updatedTicket.getOwner().getId() != owner.getId() && !isOwnerAdminOrSupport) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if(updatedTicket.getStatus() == TicketStatus.CLOSED) {
            return new ResponseEntity<>(HttpStatus.METHOD_NOT_ALLOWED);
        }

        TicketComment comment;
        TicketComment replyData = ticketReply.comment();

        if(replyData.getReplyTo() != null) {
            comment = new TicketComment(updatedTicket, owner, ticketReply.comment().getContent(), replyData.getReplyTo());
        }else {
            comment = new TicketComment(updatedTicket, owner, ticketReply.comment().getContent());
        }

        updatedTicket.addComment(comment);

        ticketRepository.save(updatedTicket);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/edit")
    public ResponseEntity<HttpStatus> edit(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                           @RequestBody TicketEdit ticketEdit) {
        Optional<User> userOptional = getUser(principal);

        if(userOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        User user = userOptional.get();

        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketEdit.ticket());

        if(ticketOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ticket ticket = ticketOptional.get();

        if(ticket.getOwner().getId() != user.getId()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String changedTo = ticketEdit.changeTo();

        if(changedTo.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            ticket.setBody(ticketEdit.changeTo());

            ticketRepository.save(ticket);

            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @PostMapping("/update-status")
    public ResponseEntity<HttpStatus> updateStatus(@AuthenticationPrincipal OAuth2AuthenticatedPrincipal principal,
                                                      @RequestBody TicketStatusUpdate ticketStatusUpdate) {
        // TODO track who updated status (i.e. statusLastUpdatedBy in Ticket)
        Optional<User> user = getUser(principal);

        if(user.isEmpty()) { return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); }

        if(!isSupportOrAdminRole(user.get())) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketStatusUpdate.ticket());

        Ticket ticket;

        if(ticketOptional.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else{
            ticket = ticketOptional.get();
        }

        TicketStatus newStatus = ticketStatusUpdate.status();

        if(newStatus == ticket.getStatus()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        ticket.setStatus(ticketStatusUpdate.status());

        ticketRepository.save(ticket);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<HttpStatus> update(@RequestBody Ticket ticket) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> delete(@RequestBody Ticket ticket) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
