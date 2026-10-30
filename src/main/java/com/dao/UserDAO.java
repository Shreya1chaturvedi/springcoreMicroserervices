package com.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.beans.User;

@Repository
public class UserDAO {

    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int save(User user) {

        String sql =
            "INSERT INTO users(email, password, role) VALUES (?, ?, ?)";

        //return jdbcTemplate.update(
        //    sql,
        //    user.getEmail(),
        //    user.getPassword(),
        //    user.getRole()
       // );
        
        int rows =  jdbcTemplate.update(
                sql,
                user.getEmail(),
                user.getPassword(),
                user.getRole()
            );
        
        System.out.println("Rows inserted: " + rows);
        return rows;
        
    }

    public User findByEmail(String email) {

        String sql =
            "SELECT * FROM users WHERE email = ?";

        return jdbcTemplate.query(
            sql,
            rs -> {

                if (rs.next()) {

                    User user = new User();

                    user.setUserId(rs.getLong("user_id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));

                    return user;
                }

                return null;
            },
            email
        );
    }
}