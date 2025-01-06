package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Ingredient;
import com.javaintellij.examenjava.Entities.PlatPrincipal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlatPrincipalDAO {
    private static final String INSERT_PLAT =
            "INSERT INTO plats_principal(nom, prix_base) VALUES (?, ?)";
    private static final String SELECT_PLAT =
            "SELECT * FROM plats_principal WHERE id = ?";
    private static final String UPDATE_PLAT =
            "UPDATE plats_principal SET nom = ?, prix_base = ? WHERE id = ?";
    private static final String DELETE_PLAT =
            "DELETE FROM plats_principal WHERE id = ?";
    private static final String SELECT_ALL_PLATS =
            "SELECT * FROM plats_principal";
    private static final String INSERT_PLAT_INGREDIENT =
            "INSERT INTO plat_ingredients (plat_id, ingredient_id, quantite) VALUES (?, ?, ?)";
    private static final String UPDATE_PLAT_INGREDIENT =
            "UPDATE plat_ingredients SET quantite = ? WHERE plat_id = ? AND ingredient_id = ?";
    private static final String DELETE_PLAT_INGREDIENTS =
            "DELETE FROM plat_ingredients WHERE plat_id = ?";
    private static final String SELECT_PLAT_INGREDIENTS =
            "SELECT i.*, pi.quantite FROM ingredients i " +
                    "JOIN plat_ingredients pi ON i.id = pi.ingredient_id " +
                    "WHERE pi.plat_id = ?";
    private static final String SELECT_PLAT_BY_NAME =
            "SELECT * FROM plats_principaux WHERE nom = ?";

    private Connection connection;
    private IngredientDAO ingredientDAO;

    public PlatPrincipalDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
        this.ingredientDAO = new IngredientDAO();
    }

    public void create(PlatPrincipal plat) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Insertion du plat principal
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_PLAT,
                    Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setString(1, plat.getNom());
                pstmt.setDouble(2, plat.getPrixBase());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        plat.setId(generatedKeys.getInt(1));
                    }
                }
            }

            // Insertion des ingrédients du plat
            insertPlatIngredients(plat);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    private void insertPlatIngredients(PlatPrincipal plat) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(INSERT_PLAT_INGREDIENT)) {
            for (Map.Entry<Ingredient, Double> entry : plat.getIngredients().entrySet()) {
                pstmt.setInt(1, plat.getId());
                pstmt.setInt(2, entry.getKey().getId());
                pstmt.setDouble(3, entry.getValue());
                pstmt.executeUpdate();
            }
        }
    }

    public PlatPrincipal read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_PLAT)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );

                // Chargement des ingrédients
                loadPlatIngredients(plat);

                return plat;
            }
            return null;
        }
    }

    private void loadPlatIngredients(PlatPrincipal plat) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_PLAT_INGREDIENTS)) {
            pstmt.setInt(1, plat.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire")
                );
                plat.ajouterIngredient(ingredient, rs.getDouble("quantite"));
            }
        }
    }

    public List<PlatPrincipal> findAll() throws SQLException {
        List<PlatPrincipal> plats = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_PLATS)) {

            while (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );
                loadPlatIngredients(plat);
                plats.add(plat);
            }
        }
        return plats;
    }

    public void update(PlatPrincipal plat) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Mise à jour des informations de base du plat
            try (PreparedStatement pstmt = connection.prepareStatement(UPDATE_PLAT)) {
                pstmt.setString(1, plat.getNom());
                pstmt.setDouble(2, plat.getPrixBase());
                pstmt.setInt(3, plat.getId());
                pstmt.executeUpdate();
            }

            // Mise à jour des ingrédients
            // D'abord supprimer tous les ingrédients existants
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PLAT_INGREDIENTS)) {
                pstmt.setInt(1, plat.getId());
                pstmt.executeUpdate();
            }

            // Puis réinsérer les nouveaux ingrédients
            insertPlatIngredients(plat);

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public void delete(int id) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Suppression des ingrédients associés
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PLAT_INGREDIENTS)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            // Suppression du plat
            try (PreparedStatement pstmt = connection.prepareStatement(DELETE_PLAT)) {
                pstmt.setInt(1, id);
                pstmt.executeUpdate();
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    // Méthodes additionnelles utiles
    public List<PlatPrincipal> findByIngredient(Ingredient ingredient) throws SQLException {
        List<PlatPrincipal> plats = new ArrayList<>();
        String query = "SELECT DISTINCT p.* FROM plats_principaux p " +
                "JOIN plat_ingredients pi ON p.id = pi.plat_id " +
                "WHERE pi.ingredient_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, ingredient.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );
                loadPlatIngredients(plat);
                plats.add(plat);
            }
        }
        return plats;
    }

    public List<PlatPrincipal> findByPrixRange(double minPrix, double maxPrix) throws SQLException {
        List<PlatPrincipal> plats = new ArrayList<>();
        String query = "SELECT * FROM plats_principaux WHERE prix_base BETWEEN ? AND ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, minPrix);
            pstmt.setDouble(2, maxPrix);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );
                loadPlatIngredients(plat);
                plats.add(plat);
            }
        }
        return plats;
    }

    public PlatPrincipal findByName(String nom) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_PLAT_BY_NAME)) {
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlatPrincipal plat = new PlatPrincipal(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_base")
                );

                // Chargement des ingrédients du plat
                loadPlatIngredients(plat);

                return plat;
            }
            return null; // Si aucun plat avec ce nom n'a été trouvé
        }
    }
}
