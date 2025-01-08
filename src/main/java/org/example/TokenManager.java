package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TokenManager {
    // Method to generate and store a new token in the database
    public String generateToken(String username) {
        String token = username + "-mtcgToken"; // Token format

        // Check if the token already exists in the database
        if (validateToken(token)) {
            System.out.println("Token already exists for user: " + username);
            return token; // Return the existing token
        }

        // If token doesn't exist, insert the new token into the database
        String query = "INSERT INTO tokens (token, username) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, token);
            preparedStatement.setString(2, username);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Token generated and stored for user: " + username);
            } else {
                System.out.println("Failed to store token for user: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Error while storing token in the database: " + e.getMessage());
        }

        return token;
    }

    public boolean validateToken(String token) {
        String query = "SELECT username FROM tokens WHERE token = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, token);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String username = resultSet.getString("username");
                System.out.println("Token is valid for user: " + username);
                return true;  // Token exists and is valid
            } else {
                System.out.println("Invalid or expired token");
            }
        } catch (SQLException e) {
            System.err.println("Error while validating token: " + e.getMessage());
        }

        return false;  // Token does not exist or is invalid
    }

}
