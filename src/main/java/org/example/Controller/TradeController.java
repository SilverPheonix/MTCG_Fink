package org.example.Controller;

import java.io.PrintWriter;

public class TradeController {
    public void createTrade(String token, String payload, PrintWriter out) {
        // Trade creation logic
        out.println("HTTP/1.1 201 Created");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"message\": \"Trade created successfully\"}");
    }


    public void acceptTrade(String token,String requestLine, String payload, PrintWriter out) {
        // Accept trade logic
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"message\": \"Trade accepted successfully\"}");
    }

    public void deleteTrade(String token,String requestLine, PrintWriter out) {
        // Delete trade logic
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"message\": \"Trade deleted successfully\"}");
    }
}
