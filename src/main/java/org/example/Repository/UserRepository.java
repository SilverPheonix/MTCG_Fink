package org.example.Repository;

import org.example.DatabaseConnection;
import org.example.Model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserRepository {

    // Method to add a new user to the database
    public boolean addUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            System.out.println("Add User failed: User or username is null/empty.");
            return false;
        }
        // Check if the username already exists in the database
        if (getUserByUsername(user.getUsername()) != null) {
            System.out.println("Add User failed: Username already exists: " + user.getUsername());
            return false;  // Username already exists
        }

        String query = "INSERT INTO users (id, username, password, coins, elo) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setObject(1, UUID.randomUUID()); // Automatically generate UUID
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getPassword());
            preparedStatement.setInt(4, user.getCoins());
            preparedStatement.setInt(5, user.getElo());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User added successfully: " + user.getUsername());
                return true;
            } else {
                System.out.println("Failed to add user: " + user.getUsername());
            }

        } catch (SQLException e) {
            System.err.println("Error while adding user to the database: " + e.getMessage());
        }

        return false;
    }

    // Method to validate user credentials
    public boolean validateUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            System.out.println("User validation failed: User or username cannot be null/empty");
            return false;
        }

        String query = "SELECT password FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getUsername());
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String storedPassword = resultSet.getString("password");
                if (storedPassword.equals(user.getPassword())) {
                    System.out.println("User validated successfully: " + user.getUsername());
                    return true;
                } else {
                    System.out.println("User validation failed: Wrong credentials");
                }
            } else {
                System.out.println("User validation failed: User does not exist");
            }

        } catch (SQLException e) {
            System.err.println("Error while validating user: " + e.getMessage());
        }

        return false;
    }

    // Method to fetch a user by username
    public User getUserByUsername(String username) {
        if (username == null || username.isEmpty()) {
            System.out.println("Get User failed: Username cannot be null/empty");
            return null;
        }

        String query = "SELECT username, password, coins, elo FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                User user = new User();
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setCoins(resultSet.getInt("coins"));
                user.setElo(resultSet.getInt("elo"));
                System.out.println("User fetched successfully: " + user.getUsername());
                return user;
            } else {
                System.out.println("No user found with username: " + username);
            }

        } catch (SQLException e) {
            System.err.println("Error while fetching user: " + e.getMessage());
        }

        return null;
    }

    // Method to update user data
    public boolean updateUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().isEmpty()) {
            System.out.println("Update User failed: User or username is null/empty.");
            return false;
        }

        String query = "UPDATE users SET password = ?, coins = ?, elo = ? WHERE username = ?";

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user.getPassword());
            preparedStatement.setInt(2, user.getCoins());
            preparedStatement.setInt(3, user.getElo());
            preparedStatement.setString(4, user.getUsername());

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("User updated successfully: " + user.getUsername());
                return true;
            } else {
                System.out.println("Failed to update user: " + user.getUsername());
            }

        } catch (SQLException e) {
            System.err.println("Error while updating user in the database: " + e.getMessage());
        }

        return false;
    }
}
