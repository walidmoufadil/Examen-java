package com.javaintellij.examenjava.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Getter@Setter
public class Repas {
    private int id;
    private PlatPrincipal platPrincipal;
    private List<Supplement> supplements;
    private Map<Ingredient, Double> ingredientsPersonnalises;

    public Repas(int id, PlatPrincipal platPrincipal) {
        this.id = id;
        this.platPrincipal = platPrincipal;
        this.supplements = new ArrayList<>();
        this.ingredientsPersonnalises = new HashMap<>();
    }

    public void ajouterSupplement(Supplement supplement) {
        supplements.add(supplement);
    }

    public void personnaliserIngredient(Ingredient ingredient, double quantite) {
        ingredientsPersonnalises.put(ingredient, quantite);
    }

    public double calculerTotal() {
        double total = platPrincipal.calculerPrix();

        // Ajouter le prix des suppléments
        for (Supplement supplement : supplements) {
            total += supplement.getPrix();
        }

        // Ajouter le prix des ingrédients personnalisés
        for (Map.Entry<Ingredient, Double> entry : ingredientsPersonnalises.entrySet()) {
            total += entry.getKey().calculerPrix(entry.getValue());
        }

        return total;
    }

}
