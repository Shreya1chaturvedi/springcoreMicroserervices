package com.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beans.Flight;
import com.beans.User;
import com.services.FlightService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final FlightService flightService;

    public AdminController(FlightService flightService) {
        this.flightService = flightService;
    }

    private boolean isAdmin(HttpSession session) {

        Object user = session.getAttribute("user");

        return user instanceof User &&
            "ADMIN".equalsIgnoreCase(((User) user).getRole());
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        return "admin-dashboard";
    }

    @GetMapping("/add-flight")
    public String addFlightPage(HttpSession session, Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("flight", new Flight());

        return "add-flight";
    }

    @PostMapping("/add-flight")
    public String addFlight(
            HttpSession session,
            @ModelAttribute Flight flight,
            Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        boolean added = flightService.add(flight);

        if (!added) {
            model.addAttribute("error", "Flight number already exists");
            return "add-flight";
        }

        return "redirect:/admin/view-flights";
    }

    @GetMapping("/view-flights")
    public String viewFlights(HttpSession session, Model model) {

        if (!isAdmin(session)) {
            return "redirect:/login";
        }

        model.addAttribute("upcoming", flightService.upcoming());
        model.addAttribute("completed", flightService.completed());

        return "view-flights";
    }
}
