package com.javaintellij.examenjava.Entities;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Ingredient {
    private int id;
    private String nom;
    private double prixUnitaire;

    public Ingredient(int id, String nom, double prixUnitaire) {
        this.id = id;
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
    }
    public Ingredient(String nom, double prixUnitaire) {
        this.nom = nom;
        this.prixUnitaire = prixUnitaire;
    }

    public double calculerPrix(double quantite) {
        return prixUnitaire * quantite;
    }

}

