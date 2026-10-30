package com.dao;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beans.Flight;

@Repository
public class FlightDAO {

    private final JdbcTemplate jdbcTemplate;
    
    public FlightDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(Flight flight) {

        String sql =
            "INSERT INTO flights " +
            "(flight_number, source, destination, departure_time, " +
            "arrival_time, capacity, available_seats, price_per_seat) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        return jdbcTemplate.update(
            sql,
            flight.getFlightNumber(),
            flight.getSource(),
            flight.getDestination(),
            flight.getDepartureTime(),
            flight.getArrivalTime(),
            flight.getCapacity(),
            flight.getAvailableSeats(),
            flight.getPricePerSeat()
        );
    }

    public Flight findById(long id) {

        String sql =
            "SELECT * FROM flights WHERE flight_id = ?";

        return jdbcTemplate.query(
            sql,
            rs -> {
                if (rs.next()) {
                    return mapFlight(rs);
                }
                return null;
            },
            id
        );
    }

    public Flight findByNumber(String flightNumber) {

        String sql =
            "SELECT * FROM flights WHERE flight_number = ?";

        return jdbcTemplate.query(
            sql,
            rs -> {
                if (rs.next()) {
                    return mapFlight(rs);
                }
                return null;
            },
            flightNumber
        );
    }

    public List<Flight> search(String source, String destination) {

        String sql =
            "SELECT * FROM flights " +
            "WHERE LOWER(source) = LOWER(?) " +
            "AND LOWER(destination) = LOWER(?) " +
            "AND available_seats > 0";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapFlight(rs),
            source,
            destination
        );
    }

    public List<Flight> findAll() {

        String sql =
            "SELECT * FROM flights ORDER BY flight_id DESC";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapFlight(rs)
        );
    }

    public List<Flight> findBookedUpcoming() {

        String sql =
            "SELECT DISTINCT f.* " +
            "FROM flights f " +
            "JOIN bookings b ON f.flight_id = b.flight_id " +
            "WHERE b.status = 'CONFIRMED' " +
            "AND b.journey_date >= CURDATE()";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapFlight(rs)
        );
    }

    public List<Flight> findBookedPast() {

        String sql =
            "SELECT DISTINCT f.* " +
            "FROM flights f " +
            "JOIN bookings b ON f.flight_id = b.flight_id " +
            "WHERE b.status = 'CONFIRMED' " +
            "AND b.journey_date < CURDATE()";

        return jdbcTemplate.query(
            sql,
            (rs, rowNum) -> mapFlight(rs)
        );
    }

    public int updateAvailableSeats(long flightId, int seats) {

        String sql =
            "UPDATE flights " +
            "SET available_seats = available_seats + ? " +
            "WHERE flight_id = ?";

        return jdbcTemplate.update(sql, seats, flightId);
    }

    private Flight mapFlight(java.sql.ResultSet rs)
            throws java.sql.SQLException {

        Flight flight = new Flight();

        flight.setFlightId(rs.getLong("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setSource(rs.getString("source"));
        flight.setDestination(rs.getString("destination"));
        flight.setDepartureTime(rs.getString("departure_time"));
        flight.setArrivalTime(rs.getString("arrival_time"));
        flight.setCapacity(rs.getInt("capacity"));
        flight.setAvailableSeats(rs.getInt("available_seats"));
        flight.setPricePerSeat(rs.getDouble("price_per_seat"));

        return flight;
    }

	public List<Flight> findBookedCompleted() {
		// TODO Auto-generated method stub
		return null;
	}
}
