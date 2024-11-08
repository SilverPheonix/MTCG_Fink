package org.example;
import org.example.Server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server(10001);  // Listening on port 10001
        server.start();
    }
}
