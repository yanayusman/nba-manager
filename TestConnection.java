/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Chanteq Demo
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        // JDBC URL for MySQL with phpMyAdmin
        String url = "jdbc:mysql://localhost:3306/test";
        String username = "root";
        String password = "";

        try {
            // Establish connection
            Connection connection = DriverManager.getConnection(url, username, password);
            
            // Connection successful
            System.out.println("Connected to the database.");

            // Close connection when done
            connection.close();
        } catch (SQLException e) {
            // Connection error
            System.out.println("Failed to connect to the database.");
            e.printStackTrace();
        }
    }
}

