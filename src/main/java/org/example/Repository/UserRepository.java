package org.example.Repository;

import org.example.Model.User;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserRepository {

    // In-memory storage for users (will be replaced with DB)
    private Map<String, User> users = new HashMap<>();

    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new IllegalArgumentException("User or username cannot be null/empty.");
        }

        if (users.containsKey(user.getUsername())) {
            return false;  // Username already exists
        }
        users.put(user.getUsername(), user);
        return true;
    }

    public boolean validateUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            System.out.println("User validation failed: User or username cannot be null/empty" );
            return false;
        }
        User u = users.get(user.getUsername());
        if (u == null) {
            System.out.println("User validation failed: User does not exist" );
            return false; // Benutzer existiert nicht
        }

        if (!u.getPassword().equals(user.getPassword())) {
            System.out.println("User validation failed: Wrong credentials");
            return false; // Falsches Passwort
        }
        return true;
    }
}
