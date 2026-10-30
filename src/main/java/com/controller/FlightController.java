// filename: FlightController.java
package com.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.services.FlightService;
import com.beans.Flight;
import jakarta.servlet.http.HttpSession;  // ✅ ADD THIS IMPORT

@Controller
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/flights")
    public String flights(Model model) {
        model.addAttribute("flights", flightService.findAll());
        return "search-flights";
    }

    @GetMapping("/add-flight")
    public String showAddFlightForm(HttpSession session) {  // ✅ ADD session param
        // ✅ Block non-admins from add-flight page
        if (session.getAttribute("loggedInUser") == null ||
            !"ADMIN".equalsIgnoreCase(
                String.valueOf(session.getAttribute("userRole")))) {
            return "redirect:/login";
        }
        return "add-flight";
    }

    @GetMapping("/search")
    public String searchFlights(
            @RequestParam("source") String source,
            @RequestParam("destination") String destination,
            Model model) {
        // ✅ Search is public — no login needed to VIEW flights
        List<Flight> flights = flightService.search(source, destination);
        model.addAttribute("flights", flights);
        return "search-flights";
    }
}
