package com.services;

import org.springframework.stereotype.Service;

import com.beans.User;
import com.dao.UserDAO;

@Service
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean signup(User user) {

        User existing =
            userDAO.findByEmail(user.getEmail());

        if (existing != null) {
            return false;
        }

       int rows = userDAO.save(user);

        return rows > 0;
    }

    public User login(
            String email,
            String password) {

        User user =
            userDAO.findByEmail(email);

        if (user != null
                && user.getPassword().equals(password)) {

            return user;
        }

        return null;
    }
}