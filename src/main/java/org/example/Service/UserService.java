package org.example.Service;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.example.TokenManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

public class UserService {
    private UserRepository userRepository = new UserRepository();
    private TokenManager tokenManager = new TokenManager();
    private ObjectMapper objectMapper = new ObjectMapper();  // Jackson library for parsing JSON

    public String registerUser(String payload) throws IOException {
        User newUser = parseUserFromPayload(payload);

        // Add the user to the database
        if (userRepository.addUser(newUser)) {
            return "{\"message\": \"User registered successfully\"}";
        } else {
            return "{\"error\": \"User already exists\"}";
        }
    }

    public String loginUser(String payload) throws IOException {
        User user = parseUserFromPayload(payload);

        // Authenticate user
        if (userRepository.validateUser(user)) {
            String token = tokenManager.generateToken(user.getUsername());
            return "{\"token\": \"" + token + "\"}";
        } else {
            return "{\"error\": \"Invalid credentials\"}";
        }
    }

    // Method to update user data
    public String updateUserData(String username, String payload) throws IOException {
        // Parse the updated user from the payload
        User updatedUser = parseUserFromPayload(payload);

        // Ensure the user exists before attempting to update
        User existingUser = userRepository.getUserByUsername(username);
        if (existingUser != null) {
            // Preserve the original username
            updatedUser.setUsername(existingUser.getUsername());

            // Check each field and if it's null, set it to the existing value
            if (updatedUser.getPassword() == null || updatedUser.getPassword().isEmpty()) {
                updatedUser.setPassword(existingUser.getPassword());
            }
            if (updatedUser.getCoins() == 20) { // Assuming `getCoins()` returns an Integer
                updatedUser.setCoins(existingUser.getCoins());
            }
            if (updatedUser.getElo() == 100) { // Assuming `getElo()` returns an Integer
                updatedUser.setElo(existingUser.getElo());
            }

            // Now update the user in the repository
            if (userRepository.updateUser(updatedUser)) {
                return "{\"username\": \"" + updatedUser.getUsername() + "\", \"coins\": " + updatedUser.getCoins() + ", \"elo\": " + updatedUser.getElo() + "}";
            } else {
                return "{\"error\": \"Failed to update user data\"}";
            }
        } else {
            return "{\"error\": \"User not found\"}";
        }
    }

    // Method to get user data
    public String getUserData(String username) throws IOException {
        User user = userRepository.getUserByUsername(username);
        if (user != null) {
            return "{\"username\": \"" + user.getUsername() + "\", \"coins\": " + user.getCoins() + ", \"elo\": " + user.getElo() + "}";
        } else {
            return "{\"error\": \"User not found\"}";
        }
    }


    private User parseUserFromPayload(String payload) throws IOException {
        // Jackson is used here to parse JSON to Java object
        return objectMapper.readValue(payload, User.class);
    }
}
