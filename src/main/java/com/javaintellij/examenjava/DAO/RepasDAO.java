package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Ingredient;
import com.javaintellij.examenjava.Entities.PlatPrincipal;
import com.javaintellij.examenjava.Entities.Repas;
import com.javaintellij.examenjava.Entities.Supplement;

import java.sql.*;
import java.util.Map;

public class RepasDAO {
    private static final String INSERT_REPAS =
            "INSERT INTO repas (plat_principal_id) VALUES (?)";
    private static final String INSERT_REPAS_SUPPLEMENT =
            "INSERT INTO repas_supplements (repas_id, supplement_id) VALUES (?, ?)";
    private static final String INSERT_REPAS_INGREDIENT =
            "INSERT INTO repas_ingredients_personnalises (repas_id, ingredient_id, quantite) VALUES (?, ?, ?)";
    private static final String SELECT_REPAS =
            "SELECT * FROM repas WHERE id = ?";

    private Connection connection;
    private PlatPrincipalDAO platPrincipalDAO;

    public RepasDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
        this.platPrincipalDAO = new PlatPrincipalDAO();
    }

    public void create(Repas repas) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Insertion du repas
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_REPAS,
                    Statement.RETURN_GENERATED_KEYS)) {

                //pstmt.setInt(1, repas.get);
                pstmt.setInt(1, repas.getPlatPrincipal().getId());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        repas.setId(generatedKeys.getInt(1));
                    }
                }
            }

            // Insertion des suppléments
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_REPAS_SUPPLEMENT)) {
                for (Supplement supplement : repas.getSupplements()) {
                    pstmt.setInt(1, repas.getId());
                    pstmt.setInt(2, supplement.getId());
                    pstmt.executeUpdate();
                }
            }

            // Insertion des ingrédients personnalisés
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_REPAS_INGREDIENT)) {
                for (Map.Entry<Ingredient, Double> entry : repas.getIngredientsPersonnalises().entrySet()) {
                    pstmt.setInt(1, repas.getId());
                    pstmt.setInt(2, entry.getKey().getId());
                    pstmt.setDouble(3, entry.getValue());
                    pstmt.executeUpdate();
                }
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Repas read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_REPAS)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                PlatPrincipal platPrincipal = platPrincipalDAO.read(rs.getInt("plat_principal_id"));
                Repas repas = new Repas(rs.getInt("id"), platPrincipal);
                //repas.setCommandeId(rs.getInt("commande_id"));
                loadSupplements(repas);
                loadIngredientsPersonnalises(repas);

                return repas;
            }
            return null;
        }
    }

    private void loadSupplements(Repas repas) throws SQLException {
        String query = "SELECT s.* FROM supplements s " +
                "JOIN repas_supplements rs ON s.id = rs.supplement_id " +
                "WHERE rs.repas_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, repas.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Supplement supplement = new Supplement(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix")
                );
                repas.ajouterSupplement(supplement);
            }
        }
    }

    private void loadIngredientsPersonnalises(Repas repas) throws SQLException {
        String query = "SELECT i.*, rip.quantite FROM ingredients i " +
                "JOIN repas_ingredients_personnalises rip ON i.id = rip.ingredient_id " +
                "WHERE rip.repas_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, repas.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Ingredient ingredient = new Ingredient(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prix_unitaire")
                );
                repas.personnaliserIngredient(ingredient, rs.getDouble("quantite"));
            }
        }
    }
}
