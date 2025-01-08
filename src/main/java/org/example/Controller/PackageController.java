package org.example.Controller;

import org.example.Model.Card;
import org.example.Service.PackageService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class PackageController {
    public PackageService packageService = new PackageService();

    // Method to handle creating a package
    public void createPackage(String payload, String token, PrintWriter out) throws IOException {
        try {
            boolean isCreated = packageService.createPackage(payload, token);

            if (isCreated) {
                out.println("HTTP/1.1 201 Created");
                out.println("Content-Type: application/json");
                out.println();
                out.println("{\"message\": \"Package successfully created.\"}");
            } else {
                out.println("HTTP/1.1 409 Conflict");
                out.println("Content-Type: application/json");
                out.println();
                out.println("{\"error\": \"At least one card in the package already exists.\"}");
            }
        } catch (IllegalArgumentException e) {
            out.println("HTTP/1.1 400 Bad Request");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) {
            out.println("HTTP/1.1 403 Forbidden");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    // Method to handle buying a package
    public void buyPackage(String token, PrintWriter out) throws IOException {
        try {
            List<Card> purchasedCards = packageService.buyPackage(token);

            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: application/json");
            out.println();
            out.println(purchasedCards); // Convert list to JSON (e.g., using a JSON library)
        } catch (IllegalArgumentException e) {
            out.println("HTTP/1.1 400 Bad Request");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (SecurityException e) {
            out.println("HTTP/1.1 403 Forbidden");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        } catch (IllegalStateException e) {
            out.println("HTTP/1.1 404 Not Found");
            out.println("Content-Type: application/json");
            out.println();
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}
