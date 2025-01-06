package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Ingredient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredientDAO {
    private static final String INSERT_INGREDIENT =
            "INSERT INTO ingredient (nom, prix_unitaire) VALUES (?, ?)";
    private static final String SELECT_INGREDIENT =
            "SELECT * FROM ingredient WHERE id = ?";
    private static final String UPDATE_INGREDIENT =
            "UPDATE ingredient SET nom = ?, prix_unitaire = ? WHERE id = ?";
    private static final String DELETE_INGREDIENT =
            "DELETE FROM ingredient WHERE id = ?";
    private static final String SELECT_ALL_INGREDIENTS =
            "SELECT * FROM ingredient";

    private Connection connection;

    public IngredientDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
    }

    public void create(Ingredient ingredient) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_INGREDIENT,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, ingredient.getNom());
            pstmt.setDouble(2, ingredient.getPrixUnitaire());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ingredient.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Ingredient read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_INGREDIENT)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Ingredient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire")
                );
            }
            return null;
        }
    }

    public List<Ingredient> findAll() throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_INGREDIENTS)) {

            while (rs.next()) {
                ingredients.add(new Ingredient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire")
                ));
            }
        }
        return ingredients;
    }

    public void update(Ingredient ingredient) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_INGREDIENT)) {
            pstmt.setString(1, ingredient.getNom());
            pstmt.setDouble(2, ingredient.getPrixUnitaire());
            pstmt.setInt(3, ingredient.getId());
            pstmt.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(DELETE_INGREDIENT)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}
