package org.example;

import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.HashMap;
import org.example.Controller.*;

public class Server {
    private int port;
    private TokenManager tokenManager;
    private UserController userController = new UserController();
    private CardController cardController = new CardController();
    private BattleController battleController = new BattleController();
    private TradeController tradeController = new TradeController();
    private GameController gameController = new GameController();
    private PackageController packageController = new PackageController(); // Add PackageController

    public Server(int port) {
        this.port = port;
        this.tokenManager = new TokenManager();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                // Bearbeite jede Anfrage in einem separaten Thread
                new Thread(() -> {
                    try {
                        handleRequest(clientSocket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRequest(Socket clientSocket) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String requestLine = in.readLine();
        StringBuilder payload = new StringBuilder();

        Map<String, String> headers = parseHeaders(in);
        String untrimmedToken = headers.get("Authorization");

        int contentLength = headers.containsKey("Content-Length") ? Integer.parseInt(headers.get("Content-Length")) : 0;
        if (contentLength > 0) {
            char[] body = new char[contentLength];
            int read = in.read(body, 0, contentLength);
            payload.append(body, 0, read);
        }

        if (requestLine != null) {
            // Public Endpoints
            if (requestLine.startsWith("POST /users")) {
                userController.registerUser(payload.toString(), out);
            } else if (requestLine.startsWith("POST /sessions")) {
                userController.loginUser(payload.toString(), out);
            }

            // Protected Endpoints (token required)
            else if (requestLine.startsWith("GET /cards")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                cardController.getCards(token, out);
            }else if (requestLine.startsWith("GET /deck")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                cardController.getDeck(token, out);
            } else if (requestLine.startsWith("PUT /deck")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                cardController.configureDeck(token, payload.toString(), out);
            } else if (requestLine.startsWith("POST /battles")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                battleController.enterBattle(token, out);
            } else if (requestLine.startsWith("GET /scoreboard")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                gameController.getScoreboard(out);
            } else if (requestLine.startsWith("POST /tradings")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                tradeController.createTrade(token, payload.toString(), out);
            } else if (requestLine.startsWith("POST /tradings/")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                tradeController.acceptTrade(token, requestLine, payload.toString(), out);
            } else if (requestLine.startsWith("DELETE /tradings/")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                tradeController.deleteTrade(token, requestLine, out);
            } else if (requestLine.startsWith("POST /packages")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                packageController.createPackage(payload.toString(), token,out);
            } else if (requestLine.startsWith("POST /transactions/packages")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                packageController.buyPackage(token, out);
            } else if (requestLine.startsWith("GET /stats")) {
                String token = untrimmedToken.substring(7).trim();
                if (!validateToken(token, out)) return;
                gameController.getStats(token, out);
            } else if (requestLine.startsWith("GET /users/")) {
                String token = untrimmedToken.substring(7).trim();
                String username = extractUsernameFromRequest(requestLine);
                if (!validateToken(token, out)) return;
                userController.getUserData(username, out);
            } else if (requestLine.startsWith("PUT /users/")) {
                String token = untrimmedToken.substring(7).trim();
                String username = extractUsernameFromRequest(requestLine);
                if (!validateToken(token, out)) return;
                userController.updateUserData(username, payload.toString(), out);
            } else {
                out.println("HTTP/1.1 404 Not Found");
                out.println();
            }
        }

        clientSocket.close();
    }

    private String extractUsernameFromRequest(String requestLine) {
        // Trim the request line to remove any leading or trailing whitespace
        requestLine = requestLine.trim();
        // Split the requestLine by spaces to separate URL from HTTP version
        String[] parts = requestLine.split(" ");
        // The URL part should be at index 1 if the request follows the format "GET /users/{username} HTTP/1.1"
        String urlPart = parts[1];
        // Split the URL part by '/' to get the username
        String[] urlParts = urlPart.split("/");
        // Make sure there are enough parts (expecting 3: "users" and the username)
        if (urlParts.length >= 3) {// Return the username (trimmed for safety)
            return urlParts[2].trim();
        } else { throw new IllegalArgumentException("Invalid request structure: " + requestLine);
        }
    }


    private boolean validateToken(String token, PrintWriter out) {
        if (!tokenManager.validateToken(token)) {
            sendUnauthorizedResponse(out);
            return false;
        }
        return true;
    }

    private void sendUnauthorizedResponse(PrintWriter out) {
        out.println("HTTP/1.1 401 Unauthorized");
        out.println("Content-Type: application/json");
        out.println();
        out.println("{\"error\": \"Unauthorized\"}");
    }

    private Map<String, String> parseHeaders(BufferedReader in) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            if (line.contains(":")) {
                String[] parts = line.split(": ", 2);
                headers.put(parts[0], parts[1]);
            }
        }
        return headers;
    }
}
