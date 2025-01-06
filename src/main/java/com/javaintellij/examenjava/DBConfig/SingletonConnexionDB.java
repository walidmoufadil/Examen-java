package com.javaintellij.examenjava.DBConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SingletonConnexionDB {
    private static Connection connection;
    private static final String URL = "jdbc:mysql://localhost:3306/restaurantdb";
    private static final String USER = "root";
    private static final String PASSWORD = "walid123"; // À modifier selon votre configuration

    private SingletonConnexionDB() {}

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
