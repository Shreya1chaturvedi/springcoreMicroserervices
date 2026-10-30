package com.dao;

import java.sql.Date;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beans.Booking;

@Repository
public class BookingDAO {

    private final JdbcTemplate jdbcTemplate;

    public BookingDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long save(Booking booking) {

        String sql =
            "INSERT INTO bookings " +
            "(booking_date, journey_date, seats, total_price, " +
            "status, flight_id, user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(
            sql,
            Date.valueOf(booking.getBookingDate()),
            Date.valueOf(booking.getJourneyDate()),
            booking.getSeats(),
            booking.getTotalPrice(),
            booking.getStatus(),
            booking.getFlightId(),
            booking.getUserId()
        );

        Long id =
            jdbcTemplate.queryForObject(
                "SELECT LAST_INSERT_ID()",
                Long.class
            );

        return id;
    }

    public List<Booking> findByUser(long userId) {

        String sql =
            "SELECT b.*, f.flight_number " +
            "FROM bookings b " +
            "JOIN flights f ON b.flight_id = f.flight_id " +
            "WHERE b.user_id = ? " +
            "ORDER BY b.booking_id DESC";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> {

                Booking booking = new Booking();

                booking.setBookingId(
                    rs.getLong("booking_id"));

                booking.setBookingDate(
                    rs.getDate("booking_date")
                        .toLocalDate());

                booking.setJourneyDate(
                    rs.getDate("journey_date")
                        .toLocalDate());

                booking.setSeats(
                    rs.getInt("seats"));

                booking.setTotalPrice(
                    rs.getDouble("total_price"));

                booking.setStatus(
                    rs.getString("status"));

                booking.setFlightId(
                    rs.getLong("flight_id"));

                booking.setUserId(
                    rs.getLong("user_id"));

                booking.setFlightNumber(
                    rs.getString("flight_number"));

                return booking;
            },
            userId
        );
    }

    public Booking findById(long id) {

        String sql =
            "SELECT * FROM bookings WHERE booking_id = ?";

        return jdbcTemplate.query(
            sql,
            rs -> {

                if (!rs.next()) {
                    return null;
                }

                Booking booking = new Booking();

                booking.setBookingId(
                    rs.getLong("booking_id"));

                booking.setBookingDate(
                    rs.getDate("booking_date")
                        .toLocalDate());

                booking.setJourneyDate(
                    rs.getDate("journey_date")
                        .toLocalDate());

                booking.setSeats(
                    rs.getInt("seats"));

                booking.setTotalPrice(
                    rs.getDouble("total_price"));

                booking.setStatus(
                    rs.getString("status"));

                booking.setFlightId(
                    rs.getLong("flight_id"));

                booking.setUserId(
                    rs.getLong("user_id"));

                return booking;
            },
            id
        );
    }

    public int cancel(long bookingId) {

        String sql =
            "UPDATE bookings " +
            "SET status = 'CANCELLED' " +
            "WHERE booking_id = ? " +
            "AND status = 'CONFIRMED'";

        return jdbcTemplate.update(
            sql,
            bookingId);
    }
}