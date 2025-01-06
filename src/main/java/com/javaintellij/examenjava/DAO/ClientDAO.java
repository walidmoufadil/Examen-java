package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Client;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {
    private static final String INSERT_CLIENT = "INSERT INTO client (id,nom) VALUES (?,?)";
    private static final String SELECT_CLIENT = "SELECT * FROM client WHERE id = ?";
    private static final String UPDATE_CLIENT = "UPDATE client SET nom = ? WHERE id = ?";
    private static final String DELETE_CLIENT = "DELETE FROM client WHERE id = ?";
    private static final String SELECT_ALL_CLIENTS = "SELECT * FROM client";

    private Connection connection;

    public ClientDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
    }

    public void create(Client client) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_CLIENT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, client.getId());
            pstmt.setString(2, client.getNom());
            pstmt.executeUpdate();
        }
    }

    public Client read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_CLIENT)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Client(rs.getInt("id"), rs.getString("nom"));
            }
            return null;
        }
    }

    public void update(Client client) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_CLIENT)) {
            pstmt.setString(1, client.getNom());
            pstmt.setInt(2, client.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_CLIENT)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_CLIENTS)) {

            while (rs.next()) {
                clients.add(new Client(rs.getInt("id"), rs.getString("nom")));
            }
        }
        return clients;
    }
}