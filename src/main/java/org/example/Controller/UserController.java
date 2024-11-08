package org.example.Controller;

import org.example.Service.UserService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

public class UserController {
    private UserService userService = new UserService();

    public void registerUser(String payload, PrintWriter out) throws IOException {

        // Call the service to register the user
        String response = userService.registerUser(payload);

        // Send HTTP response
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
    }

    public void loginUser(String payload, PrintWriter out) throws IOException {
        String response = userService.loginUser(payload);

        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println(response);
    }

}
