package com.javaintellij.examenjava.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Client {
    private int id;
    private String nom;
    List<Commande> commandeList;

    public Client(int id, String nom) {
        this.id = id;
        this.nom = nom;
        commandeList = new ArrayList<Commande>();
    }
    public void addCommande(Commande commande) {
        commandeList.add(commande);
    }

}
