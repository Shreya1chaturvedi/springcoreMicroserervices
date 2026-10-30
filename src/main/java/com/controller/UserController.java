package com.controller;

import jakarta.servlet.http.HttpSession;



import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beans.User;
import com.services.UserService;

@Controller
//@RequestMapping("/FlightWeb")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService) {

        this.userService = userService;
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String role,
            Model model) {

        User user = new User();

        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        boolean created =
            userService.signup(user);

        if (!created) {

            model.addAttribute(
                "error",
                "Email already exists");

            return "signup";
        }

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        User user =
            userService.login(
                email,
                password);

        if (user == null) {

            model.addAttribute(
                "error",
                "Invalid email or password");

            return "login";
        }

        session.setAttribute(
            "user",
            user);

        if ("ADMIN".equalsIgnoreCase(
                user.getRole())) {

            return "redirect:/admin/dashboard";
        }

        return "redirect:/user/dashboard";
    }

    @GetMapping("/logout")
    public String logout(
            HttpSession session) {

        session.invalidate();

        return "redirect:/";
    }

    @GetMapping("/user/dashboard")
    public String dashboard(
            HttpSession session) {

        if (session.getAttribute("user")
                == null) {

            return "redirect:/login";
        }

        return "user-dashboard";
    }

}