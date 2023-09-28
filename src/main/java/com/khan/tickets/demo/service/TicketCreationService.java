package com.khan.tickets.demo.service;

import com.khan.tickets.demo.model.Ticket;
import com.khan.tickets.demo.model.TicketStatus;
import com.khan.tickets.demo.model.User;
import com.khan.tickets.demo.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class TicketCreationService {

    @Autowired
    private TicketRepository ticketRepository;

    private static final TicketStatus DEFAULT_TICKET_STATUS = TicketStatus.UNASSIGNED;

    /**
     * Creates a new ticket provided the owner and the data for the Ticket.
     * @param owner - The User that is creating the ticket.
     * @param data - An instance of Ticket (provided by the RequestBody)
     * @return true if successful, false if an error occurred with the repository
     */
    public boolean createTicket(User owner, Ticket data) {
        try {
            Ticket newTicket = new Ticket(owner, data.getTitle(), data.getType(), DEFAULT_TICKET_STATUS, data.getBody());

            ticketRepository.save(newTicket);

            return true;
        }catch(IllegalArgumentException | OptimisticLockingFailureException e) {
            return false;
        }
    }
}
