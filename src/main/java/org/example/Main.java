package org.example;

import java.sql.*;

public class Main {
    private static final String url = "jdbc:mysql://localhost:3306/universities_application";
    private static final String username = "university_user";
    private static final String password = "university_pass";

    public static void main(String[] args) {

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Connected to the database");

        MenuNavigation menuNavigation = new MenuNavigation(conn);
        menuNavigation.start();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

