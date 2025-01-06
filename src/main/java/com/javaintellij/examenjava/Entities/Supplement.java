package com.javaintellij.examenjava.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class Supplement {
    private int id;
    private String nom;
    private double prix;

    public Supplement(int id, String nom, double prix) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
    }
    public Supplement(String nom, double prix) {
        this.nom = nom;
        this.prix = prix;
    }

}
