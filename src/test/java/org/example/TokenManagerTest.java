package org.example;

import org.example.Model.User;
import org.example.Repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenManagerTest {

    private TokenManager tokenManager;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // Initialize real UserRepository that connects to a real database (or in-memory DB)
        userRepository = new UserRepository();
        tokenManager = new TokenManager();
    }

    @Test
    void testValidateToken_InvalidToken() {
        String token = "invalidToken";

        // Validate that the token is invalid
        assertFalse(tokenManager.validateToken(token), "Token should be invalid.");
    }
}
