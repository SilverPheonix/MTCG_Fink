package org.example.Controller;

import org.example.Model.User;
import org.example.Service.UserService;

import java.io.IOException;
import java.io.PrintWriter;

public class UserController {
    public UserService userService = new UserService();

    public void registerUser(String payload, PrintWriter out) throws IOException {
        // Call the service to register the user
        String response = userService.registerUser(payload);
        if (response.contains("User registered successfully")) {
            // Successful registration
            out.println("HTTP/1.1 201 Created");
        } else if (response.contains("User already exists")) {
            // User already exists
            out.println("HTTP/1.1 409 User already exists");
        } else {
            // General failure
            out.println("HTTP/1.1 400 Bad Request");
        }

        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
    }

    public void loginUser(String payload, PrintWriter out) throws IOException {
        String response = userService.loginUser(payload);
        // Check if the response contains an error message (for invalid credentials)
        if (response.contains("\"error\":")) {
            // Return 401 Unauthorized or another 4xx status code
            out.println("HTTP/1.1 401 Login failed");
            out.println("Content-Type: application/json");
            out.println();
            out.println(response);
        } else {
            // Return 200 OK if the response is a valid token
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(response);
        }
    }

    // Method to handle getting user data
    public void getUserData(String username, PrintWriter out) throws IOException {
        String response = userService.getUserData(username);
        if (response.contains("\"username\":")) {
            out.println("HTTP/1.1 200 OK");
        }
        else {
            out.println("HTTP/1.1 404 User Not Found");
        }

        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
    }

    // Method to handle updating user data
    public void updateUserData(String username, String payload, PrintWriter out) throws IOException {
        String response = userService.updateUserData(username, payload);

        if (response.contains("\"username\":")) {
            out.println("HTTP/1.1 200 - User sucessfully updated");
        }
        else {
            // General failure
            out.println("HTTP/1.1 404 User not found");
        }

        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
    }
}
