package com.javaintellij.examenjava.Entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter@Setter
@NoArgsConstructor
public class Commande {
    private int id;
    private Client client;
    private List<Repas> repas;
    private Date date;

    public Commande(int id, Client client) {
        this.id = id;
        this.client = client;
        this.repas = new ArrayList<>();
//        this.date = new Date();
    }

    public void ajouterRepas(Repas repas) {
        this.repas.add(repas);
    }

    public double calculerTotal() {
        return repas.stream()
                .mapToDouble(Repas::calculerTotal)
                .sum();
    }

    public String genererTicket() {
        StringBuilder ticket = new StringBuilder();
        ticket.append("Bienvenue ").append(client.getNom()).append("\n");
        ticket.append("-".repeat(40)).append("\n");
        ticket.append("-------------TICKET-------------\n");
        ticket.append("Nom:").append(client.getNom()).append("\n\n");
        ticket.append("nombre de repas:").append(repas.size()).append("\n");

        for (int i = 0; i < repas.size(); i++) {
            Repas r = repas.get(i);
            ticket.append("Repas N°:").append(i + 1).append(" ")
                    .append(r.getPlatPrincipal().getNom()).append("\n");
            ticket.append("Ingredient:\n");

            for (Map.Entry<Ingredient, Double> ing : r.getPlatPrincipal().getIngredients().entrySet()) {
                ticket.append(ing.getKey().getNom()).append(": ")
                        .append(ing.getValue()).append(" gramme\n");
            }

            ticket.append("Supplements:\n");
            for (Supplement sup : r.getSupplements()) {
                ticket.append(sup.getNom()).append(" ").append(sup.getPrix()).append("\n");
            }
            ticket.append("********\n");
        }

        ticket.append("-------Total:").append(String.format("%.2f", calculerTotal())).append("\n");
        ticket.append("-".repeat(40)).append("\n");

        return ticket.toString();
    }
}