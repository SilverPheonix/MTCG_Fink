package org.example;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class TokenManager {
    private Map<String, String> tokenStore = new HashMap<>();

    public String generateToken(String username) {
        String token = Base64.getEncoder().encodeToString((username + "-mtcgToken").getBytes());
        tokenStore.put(token, username);
        return token;
    }

    public boolean validateToken(String token) {
        return tokenStore.containsKey(token);
    }
}
