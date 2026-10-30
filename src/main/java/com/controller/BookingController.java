package com.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beans.Flight;
import com.beans.Passenger;
import com.beans.User;
import com.services.BookingService;
import com.services.FlightService;

import jakarta.servlet.http.HttpSession;

@Controller
public class BookingController {

    private final BookingService bookingService;
    private final FlightService flightService;

    public BookingController(
            BookingService bookingService,
            FlightService flightService) {

        this.bookingService = bookingService;
        this.flightService = flightService;
    }

    // =========================
    // BOOKING PAGE
    // =========================

    @GetMapping("/booking")
    public String bookingPage(
            @RequestParam long flightId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "flight",
                flightService.get(flightId)
        );

        return "booking";
    }


    // =========================
    // START BOOKING
    // =========================

    @PostMapping("/booking")
    public String startBooking(
            @RequestParam long flightId,
            @RequestParam String journeyDate,
            @RequestParam int passengers,
            HttpSession session,
            Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Flight flight = flightService.get(flightId);

        if (flight == null) {
            model.addAttribute(
                    "error",
                    "Flight not found"
            );

            return "home";
        }

        if (passengers <= 0) {
            model.addAttribute(
                    "error",
                    "Passenger count must be greater than zero"
            );

            model.addAttribute("flight", flight);

            return "booking";
        }

        if (passengers > flight.getAvailableSeats()) {
            model.addAttribute(
                    "error",
                    "Not enough seats available"
            );

            model.addAttribute("flight", flight);

            return "booking";
        }

        session.setAttribute(
                "bookingFlightId",
                flightId
        );

        session.setAttribute(
                "journeyDate",
                journeyDate
        );

        session.setAttribute(
                "passengerCount",
                passengers
        );

        session.setAttribute(
                "passengers",
                new ArrayList<Passenger>()
        );

        return "redirect:/passenger";
    }


    // =========================
    // PASSENGER PAGE
    // =========================

    @GetMapping("/passenger")
    public String passengerPage(
            HttpSession session,
            Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Integer count =
                (Integer) session.getAttribute("passengerCount");

        if (count == null) {
            return "redirect:/";
        }

        List<Passenger> passengers =
                getPassengers(session);

        int current =
                passengers.size() + 1;

        model.addAttribute(
                "currentPassenger",
                current
        );

        model.addAttribute(
                "totalPassengers",
                count
        );

        model.addAttribute(
                "newPassenger",
                new Passenger()
        );

        return "passenger";
    }


    // =========================
    // ADD PASSENGER
    // =========================

    @PostMapping("/passenger")
    public String addPassenger(
            Passenger passenger,
            HttpSession session,
            Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        Integer count =
                (Integer) session.getAttribute("passengerCount");

        if (count == null) {
            return "redirect:/";
        }

        List<Passenger> passengers =
                getPassengers(session);

        passengers.add(passenger);

        session.setAttribute(
                "passengers",
                passengers
        );

        if (passengers.size() < count) {
            return "redirect:/passenger";
        }

        return "redirect:/booking/complete";
    }


    // =========================
    // COMPLETE BOOKING
    // =========================

    @GetMapping("/booking/complete")
    public String completeBooking(
            HttpSession session) {

        User user =
                (User) session.getAttribute("user");

        Long flightId =
                (Long) session.getAttribute(
                        "bookingFlightId"
                );

        String date =
                (String) session.getAttribute(
                        "journeyDate"
                );

        List<Passenger> passengers =
                getPassengers(session);

        if (user == null ||
                flightId == null ||
                date == null ||
                passengers.isEmpty()) {

            return "redirect:/";
        }

        long bookingId =
                bookingService.createBooking(
                        user.getUserId(),
                        flightId,
                        LocalDate.parse(date),
                        passengers
                );

        session.setAttribute(
                "lastBookingId",
                bookingId
        );

        return "redirect:/booking/confirmation";
    }


    // =========================
    // BOOKING CONFIRMATION
    // =========================

    @GetMapping("/booking/confirmation")
    public String confirmation(
            HttpSession session,
            Model model) {

        Long bookingId =
                (Long) session.getAttribute(
                        "lastBookingId"
                );

        if (bookingId == null) {
            return "redirect:/";
        }

        Object booking =
                bookingService.getBooking(
                        bookingId
                );

        model.addAttribute(
                "booking",
                booking
        );

        model.addAttribute(
                "tickets",
                bookingService.getTickets(
                        bookingId
                )
        );

        return "booking-confirmation";
    }


    // =========================
    // MY BOOKINGS
    // =========================

    @GetMapping("/user/bookings")
    public String myBookings(
            HttpSession session,
            Model model) {

        User user =
                (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute(
                "bookings",
                bookingService.getUserBookings(
                        user.getUserId()
                )
        );

        return "my-bookings";
    }


    // =========================
    // MY TICKETS
    // =========================
    //
    // IMPORTANT CHANGE:
    // bookingId is now optional.
    // If it is missing, user is redirected
    // to My Bookings instead of getting
    // MissingServletRequestParameterException.
    // =========================

    @GetMapping("/user/tickets")
    public String tickets(
            @RequestParam(
                    value = "bookingId",
                    required = false
            ) Long bookingId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // No bookingId supplied
        if (bookingId == null) {
            return "redirect:/user/bookings";
        }

        model.addAttribute(
                "tickets",
                bookingService.getTickets(
                        bookingId
                )
        );

        return "tickets";
    }


    // =========================
    // CANCEL BOOKING
    // =========================

    @GetMapping("/user/cancel")
    public String cancel(
            @RequestParam long bookingId,
            HttpSession session) {

        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        bookingService.cancelBooking(
                bookingId
        );

        return "redirect:/user/bookings";
    }


    // =========================
    // GET PASSENGERS FROM SESSION
    // =========================

    @SuppressWarnings("unchecked")
    private List<Passenger> getPassengers(
            HttpSession session) {

        List<Passenger> passengers =
                (List<Passenger>) session.getAttribute(
                        "passengers"
                );

        if (passengers == null) {
            passengers =
                    new ArrayList<>();
        }

        return passengers;
    }
}
