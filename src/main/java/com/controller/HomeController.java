package com.controller;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beans.Flight;
import com.services.FlightService;


@Controller
public class HomeController {

	private final FlightService flightService;
	
	public HomeController (FlightService flightService){
		this.flightService = flightService;
	}

    @GetMapping("/")
    public String home() {
    	System.out.println("In Home");
        return "home";
    }
    
	/*
	 * @GetMapping("/signup") public String signup() { return "signup"; }
	 */
    
    
    
    @PostMapping("/search")
    public String search(@RequestParam String source, @RequestParam String destination, Model model) {
    	List<Flight> flights = flightService.search(source, destination);
    	model.addAttribute("flights", flights);
    	return "search-flights";
    }
  
}