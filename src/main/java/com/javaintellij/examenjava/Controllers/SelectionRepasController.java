package com.javaintellij.examenjava.Controllers;

import com.javaintellij.examenjava.DAO.PlatPrincipalDAO;
import com.javaintellij.examenjava.Entities.PlatPrincipal;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class SelectionRepasController {
    @FXML
    private ListView<String> listeRepas;
    @FXML
    private Button btnContinuer;

    private Stage stage;
    private PlatPrincipalDAO platDAO;
    private PlatPrincipal platSelectionne;

    public void initialize() throws SQLException {
        platDAO = new PlatPrincipalDAO();

        try {
            for (PlatPrincipal plat : platDAO.findAll()) {
                listeRepas.getItems().add(plat.getNom());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnContinuer.setOnAction(event -> {
            String platNom = listeRepas.getSelectionModel().getSelectedItem();
            if (platNom != null) {
                try {
                    platSelectionne = platDAO.findByName(platNom);
                    ouvrirChoixIngredients();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un repas.");
                alert.show();
            }
        });
    }

    private void ouvrirChoixIngredients() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("choixIngredients.fxml"));
            Parent root = loader.load();

            ChoixIngredientsController controller = loader.getController();
            controller.setPlatPrincipal(platSelectionne);

            stage.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}

