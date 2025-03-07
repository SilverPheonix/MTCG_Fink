package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn != null) {
                System.out.println("Verbindung zur Datenbank erfolgreich!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
