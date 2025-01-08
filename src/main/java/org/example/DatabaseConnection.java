package org.example;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/mydb";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";

    public static Connection connect() throws SQLException {
        Connection conn = null;
        try {
            // Verbindung zur Datenbank herstellen
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Verbindung zur Datenbank erfolgreich hergestellt!");
        } catch (SQLException e) {
            System.out.println("Fehler beim Verbinden zur Datenbank.");
            e.printStackTrace();
        }
        return conn;
    }
}