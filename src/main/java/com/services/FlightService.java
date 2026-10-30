package com.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beans.Flight;
import com.dao.FlightDAO;

@Service
public class FlightService {

    private final FlightDAO flightDAO;

    public FlightService(FlightDAO flightDAO) {
        this.flightDAO = flightDAO;
    }

    public boolean add(Flight flight) {

        Flight existing = flightDAO.findByNumber(
                flight.getFlightNumber());

        if (existing != null) {
            return false;
        }

        if (flight.getCapacity() <= 0) {
            flight.setCapacity(20);
        }

        flight.setAvailableSeats(
                flight.getCapacity());

        flightDAO.save(flight);

        return true;
    }

    public Flight get(long id) {
        return flightDAO.findById(id);
    }

    public List<Flight> search(String source, String destination) {
        return flightDAO.search(source, destination);
    }

    public List<Flight> all() {
        return flightDAO.findAll();
    }

    public List<Flight> upcoming() {
        return flightDAO.findBookedUpcoming();
    }

    public List<Flight> completed() {
        return flightDAO.findBookedCompleted();
    }

	public Object findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}
