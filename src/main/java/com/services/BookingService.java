package com.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beans.Booking;
import com.beans.Flight;
import com.beans.Passenger;
import com.beans.Ticket;
import com.dao.BookingDAO;
import com.dao.FlightDAO;
import com.dao.TicketDAO;

@Service
public class BookingService {

    private final BookingDAO bookingDAO;
    private final FlightDAO flightDAO;
    private final TicketDAO ticketDAO;

    public BookingService(
            BookingDAO bookingDAO,
            FlightDAO flightDAO,
            TicketDAO ticketDAO) {

        this.bookingDAO = bookingDAO;
        this.flightDAO = flightDAO;
        this.ticketDAO = ticketDAO;
    }

    public List<Booking> getUserBookings(
            long userId) {

        return bookingDAO.findByUser(userId);
    }

    public Booking getBooking(long bookingId) {

        return bookingDAO.findById(bookingId);
    }

    public List<Ticket> getTickets(
            long bookingId) {

        return ticketDAO.findByBooking(bookingId);
    }

    @Transactional
    public long createBooking(
            long userId,
            long flightId,
            LocalDate journeyDate,
            List<Passenger> passengers) {

        Flight flight =
            flightDAO.findById(flightId);

        if (flight == null) {
            throw new RuntimeException(
                "Flight not found");
        }

        int passengerCount =
            passengers.size();

        if (flight.getAvailableSeats()
                < passengerCount) {

            throw new RuntimeException(
                "Not enough seats available");
        }

        double totalPrice =
            passengerCount *
            flight.getPricePerSeat();

        Booking booking = new Booking();

        booking.setBookingDate(
            LocalDate.now());

        booking.setJourneyDate(
            journeyDate);

        booking.setSeats(
            passengerCount);

        booking.setTotalPrice(
            totalPrice);

        booking.setStatus(
            "CONFIRMED");

        booking.setFlightId(
            flightId);

        booking.setUserId(
            userId);

        long bookingId =
            bookingDAO.save(booking);

        int startingSeat =
            flight.getCapacity()
            - flight.getAvailableSeats()
            + 1;

        for (int i = 0;
             i < passengers.size();
             i++) {

            Passenger passenger =
                passengers.get(i);

            Ticket ticket = new Ticket();

            ticket.setPassengerName(
                passenger.getPassengerName());

            ticket.setAge(
                passenger.getAge());

            ticket.setGender(
                passenger.getGender());

            ticket.setJourneyDate(
                journeyDate);

            ticket.setSeatNo(
                "S" + (startingSeat + i));

            ticket.setBookingId(
                bookingId);

            ticket.setStatus(
                "CONFIRMED");

            ticketDAO.save(ticket);
        }

        flightDAO.updateAvailableSeats(
            flightId,
            -passengerCount);

        return bookingId;
    }

    @Transactional
    public void cancelBooking(
            long bookingId) {

        Booking booking =
            bookingDAO.findById(bookingId);

        if (booking == null) {
            throw new RuntimeException(
                "Booking not found");
        }

        if (!"CONFIRMED".equals(
                booking.getStatus())) {

            return;
        }

        bookingDAO.cancel(bookingId);

        ticketDAO.cancelByBooking(
            bookingId);

        flightDAO.updateAvailableSeats(
            booking.getFlightId(),
            booking.getSeats());
    }
}