package org.example;

import java.io.*;
import java.net.*;
import org.example.Controller.UserController;

public class Server {
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                handleRequest(clientSocket);
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

        // Verarbeite die Header, um den Body richtig zu lesen
        String headerLine;
        int contentLength = 0;

        while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
            if (headerLine.startsWith("Content-Length: ")) {
                contentLength = Integer.parseInt(headerLine.substring(16));
            }
        }
        // Lies den Body der Anfrage
        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        payload.append(body);

        if (requestLine != null) {
            // Basic Routing logic
            if (requestLine.startsWith("POST /users")) {
                new UserController().registerUser(payload.toString(), out);
            } else if (requestLine.startsWith("POST /sessions")) {
                new UserController().loginUser(payload.toString(), out);
            } else {
                out.println("HTTP/1.1 404 Not Found");
            }
        }

        clientSocket.close();
    }
}
