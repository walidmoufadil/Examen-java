package com.javaintellij.examenjava.DAO;

import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Client;
import com.javaintellij.examenjava.Entities.Commande;
import com.javaintellij.examenjava.Entities.Repas;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeDAO {
    private static final String INSERT_COMMANDE =
            "INSERT INTO commande (client_id) VALUES (?)";
    private static final String SELECT_COMMANDE =
            "SELECT * FROM commande WHERE id = ?";
    private static final String SELECT_REPAS_BY_COMMANDE =
            "SELECT * FROM repas WHERE commande_id = ?";

    private Connection connection;
    private RepasDAO repasDAO;
    private ClientDAO clientDAO;

    public CommandeDAO() throws SQLException {
        this.connection = SingletonConnexionDB.getConnection();
        this.repasDAO = new RepasDAO();
        this.clientDAO = new ClientDAO();
    }

    public void create(Commande commande) throws SQLException {
        connection.setAutoCommit(false);
        try {
            // Insertion de la commande
            try (PreparedStatement pstmt = connection.prepareStatement(INSERT_COMMANDE,
                    Statement.RETURN_GENERATED_KEYS)) {

                pstmt.setInt(1, commande.getClient().getId());
                pstmt.executeUpdate();

                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        commande.setId(generatedKeys.getInt(1));
                    }
                }
            }

            // Insertion des repas
            for (Repas repas : commande.getRepas()) {
                //repas.setCommandeId(commande.getId());
                repasDAO.create(repas);
            }

            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public Commande read(int id) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_COMMANDE)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Client client = clientDAO.read(rs.getInt("client_id"));
                Commande commande = new Commande(
                        rs.getInt("id"),
                        client
                );

                // Chargement des repas
                List<Repas> repas = loadRepas(commande.getId());
                for (Repas r : repas) {
                    commande.ajouterRepas(r);
                }

                return commande;
            }
            return null;
        }
    }

    private List<Repas> loadRepas(int commandeId) throws SQLException {
        List<Repas> repas = new ArrayList<>();
        try (PreparedStatement pstmt = connection.prepareStatement(SELECT_REPAS_BY_COMMANDE)) {
            pstmt.setInt(1, commandeId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                repas.add(repasDAO.read(rs.getInt("id")));
            }
        }
        return repas;
    }
}
