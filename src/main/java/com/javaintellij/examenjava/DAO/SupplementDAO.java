package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Supplement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplementDAO {
    private static final String INSERT_SUPPLEMENT =
            "INSERT INTO supplements (nom, prix) VALUES (?, ?)";
    private static final String SELECT_SUPPLEMENT =
            "SELECT * FROM supplements WHERE id = ?";
    private static final String UPDATE_SUPPLEMENT =
            "UPDATE supplements SET nom = ?, prix = ? WHERE id = ?";
    private static final String DELETE_SUPPLEMENT =
            "DELETE FROM supplements WHERE id = ?";
    private static final String SELECT_ALL_SUPPLEMENTS =
            "SELECT * FROM supplements";

    private Connection connection;

    public SupplementDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
    }

    public void create(Supplement supplement) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_SUPPLEMENT,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, supplement.getNom());
            pstmt.setDouble(2, supplement.getPrix());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    supplement.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Supplement read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_SUPPLEMENT)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Supplement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix")
                );
            }
            return null;
        }
    }

    public List<Supplement> findAll() throws SQLException {
        List<Supplement> supplements = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SUPPLEMENTS)) {

            while (rs.next()) {
                supplements.add(new Supplement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix")
                ));
            }
        }
        return supplements;
    }

    public void update(Supplement supplement) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_SUPPLEMENT)) {
            pstmt.setString(1, supplement.getNom());
            pstmt.setDouble(2, supplement.getPrix());
            pstmt.setInt(3, supplement.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_SUPPLEMENT)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
