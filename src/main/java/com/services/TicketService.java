package com.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beans.Ticket;
import com.dao.TicketDAO;

@Service
public class TicketService {

    private final TicketDAO ticketDAO;

    public TicketService(TicketDAO ticketDAO) {
        this.ticketDAO = ticketDAO;
    }

    public List<Ticket> getTickets(
            long bookingId) {

        return ticketDAO.findByBooking(
            bookingId);
    }
}