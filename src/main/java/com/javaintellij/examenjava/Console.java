package com.javaintellij.examenjava;

import com.javaintellij.examenjava.DAO.ClientDAO;
import com.javaintellij.examenjava.DAO.CommandeDAO;
import com.javaintellij.examenjava.Entities.Client;
import com.javaintellij.examenjava.Entities.Commande;
import com.javaintellij.examenjava.Entities.PlatPrincipal;
import com.javaintellij.examenjava.Entities.Repas;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Console {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            ClientDAO clientDAO = new ClientDAO();
            CommandeDAO commandeDAO = new CommandeDAO();
            boolean running = true;

            while (running) {
                System.out.println("\nManagement Console");
                System.out.println("1. Create Client");
                System.out.println("2. Read Client by ID");
                System.out.println("3. Update Client");
                System.out.println("4. Delete Client");
                System.out.println("5. List All Clients");
                System.out.println("6. Create Commande");
                System.out.println("7. Read Commande by ID");
                System.out.println("8. Generate Ticket");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: // Create Client
                        System.out.print("Enter Client ID: ");
                        int createId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter Client Name: ");
                        String createName = scanner.nextLine();

                        Client newClient = new Client(createId, createName);
                        clientDAO.create(newClient);
                        System.out.println("Client created successfully!");
                        break;

                    case 2: // Read Client
                        System.out.print("Enter Client ID: ");
                        int readId = scanner.nextInt();

                        Client client = clientDAO.read(readId);
                        if (client != null) {
                            System.out.println("Client Details: " + client);
                        } else {
                            System.out.println("Client not found.");
                        }
                        break;

                    case 3: // Update Client
                        System.out.print("Enter Client ID: ");
                        int updateId = scanner.nextInt();
                        scanner.nextLine(); // Consume newline

                        System.out.print("Enter New Client Name: ");
                        String updateName = scanner.nextLine();

                        Client updatedClient = new Client(updateId, updateName);
                        clientDAO.update(updatedClient);
                        System.out.println("Client updated successfully!");
                        break;

                    case 4: // Delete Client
                        System.out.print("Enter Client ID: ");
                        int deleteId = scanner.nextInt();

                        clientDAO.delete(deleteId);
                        System.out.println("Client deleted successfully!");
                        break;

                    case 5: // List All Clients
                        List<Client> clients = clientDAO.findAll();
                        System.out.println("Clients List:");
                        for (Client c : clients) {
                            System.out.println(c);
                        }
                        break;

                    case 6: // Create Commande
                        System.out.print("Enter Client ID for the Commande: ");
                        int clientId = scanner.nextInt();
                        Client commandeClient = clientDAO.read(clientId);

                        if (commandeClient != null) {
                            Commande newCommande = new Commande();
                            newCommande.setClient(commandeClient);

                            System.out.print("Enter number of meals for the Commande: ");
                            int mealCount = scanner.nextInt();
                            scanner.nextLine();

                            List<Repas> repasList = new ArrayList<>();
                            for (int i = 0; i < mealCount; i++) {
                                System.out.print("Enter Meal " + (i + 1) + " ID: ");
                                int mealId = scanner.nextInt();
                                scanner.nextLine(); // Consume newline
                                System.out.print("Enter Meal " + (i + 1) + " Name: ");
                                String mealName = scanner.nextLine();
                                System.out.print("Enter Meal " + (i + 1) + " Name: ");
                                Float mealPrice = scanner.nextFloat();


                                repasList.add(new Repas(mealId, new PlatPrincipal(mealId,mealName,mealPrice)));
                            }

                            newCommande.setRepas(repasList);
                            commandeDAO.create(newCommande);
                            System.out.println("Commande created successfully!");
                        } else {
                            System.out.println("Client not found.");
                        }
                        break;

                    case 7: // Read Commande
                        System.out.print("Enter Commande ID: ");
                        int commandeId = scanner.nextInt();

                        Commande commande = commandeDAO.read(commandeId);
                        if (commande != null) {
                            System.out.println("Commande Details: ");
                            System.out.println("Client: " + commande.getClient());
                            System.out.println("Meals: ");
                            for (Repas repas : commande.getRepas()) {
                                System.out.println(repas);
                            }
                        } else {
                            System.out.println("Commande not found.");
                        }
                        break;

                    case 8: // Generate Ticket
                        System.out.print("Enter Commande ID: ");
                        int ticketCommandeId = scanner.nextInt();

                        Commande ticketCommande = commandeDAO.read(ticketCommandeId);
                        if (ticketCommande != null) {
                            System.out.println("Bienvenue " + ticketCommande.getClient().getNom());
                            System.out.println("-----------------TICKET------------------");
                            System.out.println("Nom: " + ticketCommande.getClient().getNom());
                            System.out.println();
                            System.out.println("Nombre de repas: " + ticketCommande.getRepas().size());

                            double total = 0;
                            int mealIndex = 1;
                            for (Repas repas : ticketCommande.getRepas()) {
                                System.out.println("Repas N°" + mealIndex);
                                System.out.println("Ingrédients:");
                                // Exemple d'affichage d'ingrédients (modifiable selon les propriétés réelles)
                                System.out.println("Viande: 250 grammes");
                                System.out.println("Pruneaux: 1 gramme");

                                System.out.println("Suppléments:");
                                // Exemple d'affichage de suppléments (modifiable selon les propriétés réelles)
                                System.out.println("Frites: 11.0");
                                System.out.println("Boisson: 12.0");
                                System.out.println("********");

                                // Exemple de calcul de total (modifiable selon les propriétés réelles)
                                total += 125.24; // Valeur fictive
                                mealIndex++;
                            }

                            System.out.println("----------------------------------------");
                            System.out.println("Total: " + total);
                        } else {
                            System.out.println("Commande not found.");
                        }
                        break;

                    case 9: // Exit
                        running = false;
                        System.out.println("Exiting... Goodbye!");
                        break;

                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}