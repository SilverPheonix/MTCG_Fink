package org.example.Controller;

import java.io.PrintWriter;

public class GameController {
    public void getScoreboard(PrintWriter out) {
        // Scoreboard logic
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
        out.println("[{\"username\": \"player1\", \"elo\": 120}, {\"username\": \"player2\", \"elo\": 100}]");
    }
    public void getStats(String token, PrintWriter out) {
        // Logic to retrieve stats for the user
        out.println("HTTP/1.1 200 OK");
        out.println("Content-Type: application/json");
        out.println();
    }
}
