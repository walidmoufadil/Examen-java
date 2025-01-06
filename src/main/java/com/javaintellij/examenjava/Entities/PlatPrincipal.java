package com.javaintellij.examenjava.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter@Setter
public class PlatPrincipal {
    private int id;
    private String nom;
    private double prixBase;
    private Map<Ingredient, Double> ingredients;

    public PlatPrincipal(int id, String nom, double prixBase) {
        this.id = id;
        this.nom = nom;
        this.prixBase = prixBase;
        this.ingredients = new HashMap<>();
    }

    public void ajouterIngredient(Ingredient ingredient, double quantite) {
        ingredients.put(ingredient, quantite);
    }

    public double calculerPrix() {
        double prixTotal = prixBase;
        for (Map.Entry<Ingredient, Double> entry : ingredients.entrySet()) {
            prixTotal += entry.getKey().calculerPrix(entry.getValue());
        }
        return prixTotal;
    }

}
