package com.javaintellij.examenjava.Controllers;

import com.javaintellij.examenjava.DAO.IngredientDAO;
import com.javaintellij.examenjava.DAO.SupplementDAO;
import com.javaintellij.examenjava.DBConfig.SingletonConnexionDB;
import com.javaintellij.examenjava.Entities.Ingredient;
import com.javaintellij.examenjava.Entities.PlatPrincipal;
import com.javaintellij.examenjava.Entities.Supplement;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoixIngredientsController {
    @FXML
    private ListView<String> listeIngredients;
    @FXML
    private ListView<String> listeSupplements;
    @FXML
    private Label lblTotal;
    @FXML
    private Button btnAjouterIngredient;
    @FXML
    private Button btnAjouterSupplement;
    @FXML
    private Button btnTerminer;

    private PlatPrincipal platPrincipal;
    private List<Ingredient> ingredientsChoisis = new ArrayList<>();
    private List<Supplement> supplementsChoisis = new ArrayList<>();
    private double total = 0;

    public void initialize() throws SQLException {
        IngredientDAO ingredientDAO = new IngredientDAO();
        SupplementDAO supplementDAO = new SupplementDAO();

        try {
            // Charger les ingrédients et suppléments disponibles
            for (Ingredient ingredient : ingredientDAO.findAll()) {
                listeIngredients.getItems().add(ingredient.getNom() + " - " + ingredient.getPrixUnitaire() + " €");
            }
            for (Supplement supplement : supplementDAO.findAll()) {
                listeSupplements.getItems().add(supplement.getNom() + " - " + supplement.getPrix() + " €");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        btnAjouterIngredient.setOnAction(event -> ajouterIngredient());
        btnAjouterSupplement.setOnAction(event -> ajouterSupplement());
        btnTerminer.setOnAction(event -> afficherTicket());
    }

    private void ajouterIngredient() {
        String selection = listeIngredients.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String nom = selection.split(" - ")[0];
            double prix = Double.parseDouble(selection.split(" - ")[1].replace(" €", ""));
            ingredientsChoisis.add(new Ingredient(nom, prix));
            recalculerTotal();
        }
    }

    private void ajouterSupplement() {
        String selection = listeSupplements.getSelectionModel().getSelectedItem();
        if (selection != null) {
            String nom = selection.split(" - ")[0];
            double prix = Double.parseDouble(selection.split(" - ")[1].replace(" €", ""));
            supplementsChoisis.add(new Supplement(nom, prix));
            recalculerTotal();
        }
    }

    private void recalculerTotal() {
        total = platPrincipal.getPrixBase();
        for (Ingredient ingredient : ingredientsChoisis) {
            total += ingredient.getPrixUnitaire();
        }
        for (Supplement supplement : supplementsChoisis) {
            total += supplement.getPrix();
        }
        lblTotal.setText("Total : " + total + " €");
    }

    private void afficherTicket() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("Ticket\n");
        ticket.append("Repas : ").append(platPrincipal.getNom()).append("\n");
        ticket.append("Ingrédients :\n");
        for (Ingredient ingredient : ingredientsChoisis) {
            ticket.append("- ").append(ingredient.getNom()).append("\n");
        }
        ticket.append("Suppléments :\n");
        for (Supplement supplement : supplementsChoisis) {
            ticket.append("- ").append(supplement.getNom()).append("\n");
        }
        ticket.append("Total : ").append(total).append(" €");

        Alert alert = new Alert(Alert.AlertType.INFORMATION, ticket.toString());
        alert.setHeaderText("Votre ticket");
        alert.show();
    }

    public void setPlatPrincipal(PlatPrincipal platPrincipal) {
        this.platPrincipal = platPrincipal;
        recalculerTotal();
    }
}

