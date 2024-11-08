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
            return "{\"error\": \"User registration failed\"}";
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

    private User parseUserFromPayload(String payload) throws IOException  {
        // Jackson is used here to parse JSON to Java object
        return objectMapper.readValue(payload, User.class);
    }
}
