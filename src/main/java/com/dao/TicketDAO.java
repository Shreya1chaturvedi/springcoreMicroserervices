package com.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beans.Ticket;

@Repository
public class TicketDAO {

    private final JdbcTemplate jdbcTemplate;

    public TicketDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Ticket ticket) {

        String sql =
            "INSERT INTO tickets " +
            "(passenger_name, gender, age, journey_date, " +
            "seat_no, booking_id, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
            sql,
            ticket.getPassengerName(),
            ticket.getGender(),
            ticket.getAge(),
            Date.valueOf(ticket.getJourneyDate()),
            ticket.getSeatNo(),
            ticket.getBookingId(),
            ticket.getStatus()
        );

        Long id =
            jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

        return id;
    }

    public List<Ticket> findByBooking(
            long bookingId) {

        String sql =
            "SELECT * FROM tickets " +
            "WHERE booking_id = ? " +
            "ORDER BY pnr_no";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {

                Ticket ticket = new Ticket();

                ticket.setPnrNo(
                    rs.getLong("pnr_no"));

                ticket.setPassengerName(
                    rs.getString("passenger_name"));

                ticket.setGender(
                    rs.getString("gender"));

                ticket.setAge(
                    rs.getInt("age"));

                ticket.setJourneyDate(
                    rs.getDate("journey_date")
                        .toLocalDate());

                ticket.setSeatNo(
                    rs.getString("seat_no"));

                ticket.setBookingId(
                    rs.getLong("booking_id"));

                ticket.setStatus(
                    rs.getString("status"));

                return ticket;
            },
            bookingId
        );
    }

    public int cancelByBooking(
            long bookingId) {

        String sql =
            "UPDATE tickets " +
            "SET status = 'CANCELLED' " +
            "WHERE booking_id = ?";

        return jdbcTemplate.update(
            sql,
            bookingId);
    }
}