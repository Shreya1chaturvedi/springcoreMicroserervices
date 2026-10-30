package com.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handle(
            Exception e, Model model) {
    	e.printStackTrace();
        model.addAttribute("errorMessage", e.getClass().getName() + " : " + (e.getMessage() == null ? "No error messae" : e.getMessage()));
        return "error";
    }

}
