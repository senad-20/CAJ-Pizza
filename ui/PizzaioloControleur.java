package ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import pizzas.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class PizzaioloControleur {

    private final Pizzaiolo systeme = MainInterface.SYSTEME;
    private Pizza pizzaSelectionnee;
    private Ingredient ingredientSelectionne;
    private InformationPersonnelle clientSelectionne;
    private Commande commandeSelectionnee;

    @FXML private ChoiceBox<TypePizza> choiceBoxTypeIngredient;
    @FXML private ChoiceBox<TypePizza> choiceBoxTypePizza;

    @FXML private TextField entreeNomIngredient;
    @FXML private TextField entreePrixIngredient;

    @FXML private TextField entreeNomPizza;
    @FXML private TextField entreePrixMinimalPizza;
    @FXML private TextField entreePrixVentePizza;
    @FXML private TextField entreePhotoPizza;
    @FXML private TextField entreeBeneficeUnitairePizza;
    @FXML private TextField entreeNbCommandesPizza;
    @FXML private TextField entreeBeneficeTotalPizza;

    @FXML private TextField entreeNombreTotalCommandes;
    @FXML private TextField entreeBeneficeTotalCommandes;
    @FXML private TextField entreeNbPizzasClient;
    @FXML private TextField entreeBeneficeClient;
    @FXML private TextField entreeBeneficeCommande;

    @FXML private Label labelListeIngredients;
    @FXML private Label labelListePizzas;
    @FXML private Label labelListeCommandes;

    @FXML private ListView<String> listeIngredients;
    @FXML private ListView<String> listePizzas;
    @FXML private ListView<String> listeCommandes;

    @FXML private ComboBox<String> comboBoxClients;

    @FXML
    void initialize() {
        choiceBoxTypeIngredient.getItems().setAll(TypePizza.values());
        choiceBoxTypePizza.getItems().setAll(TypePizza.values());
        rafraichirTout();
    }

    @FXML
    void actionBoutonAfficherTousIngredients(ActionEvent e) {
        majListeIngredients(systeme.getTousIngredients(), "Tous les ingrédients");
    }

    @FXML
    void actionBoutonCreerIngredient(ActionEvent e) {
        try {
            systeme.creerIngredient(entreeNomIngredient.getText(), Double.parseDouble(entreePrixIngredient.getText()));
            actionBoutonAfficherTousIngredients(null);
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Création impossible");
        }
    }

    @FXML
    void actionBoutonModifierPrixIngredient(ActionEvent e) {
        try {
            if (ingredientSelectionne == null) return;
            systeme.changerPrixIngredient(ingredientSelectionne.getNom(), Double.parseDouble(entreePrixIngredient.getText()));
            actionBoutonAfficherTousIngredients(null);
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Modification impossible");
        }
    }

    @FXML
    void actionBoutonInterdireIngredient(ActionEvent e) {
        if (ingredientSelectionne != null && choiceBoxTypeIngredient.getValue() != null) {
            systeme.interdireIngredient(ingredientSelectionne.getNom(), choiceBoxTypeIngredient.getValue());
        }
    }

    @FXML
    void actionListeSelectionIngredient(MouseEvent e) {
        String nom = listeIngredients.getSelectionModel().getSelectedItem();
        ingredientSelectionne = systeme.getTousIngredients().stream()
                .filter(i -> i.getNom().equals(nom))
                .findFirst()
                .orElse(null);
        if (ingredientSelectionne != null) {
            entreeNomIngredient.setText(ingredientSelectionne.getNom());
            entreePrixIngredient.setText(String.valueOf(ingredientSelectionne.getPrix()));
        }
    }

    @FXML
    void actionBoutonAfficherToutesPizzas(ActionEvent e) {
        labelListePizzas.setText("Toutes les pizzas");
        listePizzas.getItems().setAll(systeme.getPizzas().stream().map(Pizza::getNom).toList());
    }

    @FXML
    void actionBoutonCreerPizza(ActionEvent e) {
        Pizza p = systeme.creerPizza(entreeNomPizza.getText(), choiceBoxTypePizza.getValue());
        if (p != null) actionBoutonAfficherToutesPizzas(null);
    }

    @FXML
    void actionBoutonAjouterIngredientPizza(ActionEvent e) {
        if (pizzaSelectionnee != null && ingredientSelectionne != null) {
            systeme.ajouterIngredientPizza(pizzaSelectionnee, ingredientSelectionne.getNom());
            selectionnerPizza(pizzaSelectionnee);
        }
    }

    @FXML
    void actionBoutonSupprimerIngredientPizza(ActionEvent e) {
        if (pizzaSelectionnee != null && ingredientSelectionne != null) {
            systeme.retirerIngredientPizza(pizzaSelectionnee, ingredientSelectionne.getNom());
            selectionnerPizza(pizzaSelectionnee);
        }
    }

    @FXML
    void actionBoutonVerifierValiditeIngredientsPizza(ActionEvent e) {
        if (pizzaSelectionnee == null) return;
        Set<String> invalides = systeme.verifierIngredientsPizza(pizzaSelectionnee);
        labelListeIngredients.setText("Ingrédients invalides");
        listeIngredients.getItems().setAll(invalides);
    }

    @FXML
    void actionBoutonModifierPrixPizza(ActionEvent e) {
        try {
            if (pizzaSelectionnee == null) return;
            systeme.setPrixPizza(pizzaSelectionnee, Double.parseDouble(entreePrixVentePizza.getText()));
            selectionnerPizza(pizzaSelectionnee);
        } catch (Exception ex) {
            afficherAlerte("Erreur", "Prix invalide");
        }
    }

    @FXML
    void actionBoutonParcourirPhotoPizza(ActionEvent e) {
        if (pizzaSelectionnee == null) return;

        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(null);
        if (f == null) return;

        try {
            boolean ok = systeme.ajouterPhoto(pizzaSelectionnee, f.getAbsolutePath());
            if (!ok) {
                afficherAlerte("Erreur", "Image invalide");
                return;
            }
            entreePhotoPizza.setText(f.getName());
        } catch (IOException ex) {
            afficherAlerte("Erreur", "Impossible de charger l'image");
        }
    }

    @FXML
    void actionListeSelectionPizza(MouseEvent e) {
        String nom = listePizzas.getSelectionModel().getSelectedItem();
        pizzaSelectionnee = systeme.getPizzas().stream().filter(p -> p.getNom().equals(nom)).findFirst().orElse(null);
        if (pizzaSelectionnee != null) selectionnerPizza(pizzaSelectionnee);
    }

    @FXML
    void actionBoutonCommandesNonTraitees(ActionEvent e) {
        List<Commande> cmds = systeme.commandeNonTraitees();
        majListeCommandes(cmds, "Commandes non traitées");
        majStats();
    }

    @FXML
    void actionBoutonCommandesDejaTraitees(ActionEvent e) {
        majListeCommandes(systeme.commandesDejaTraitees(), "Commandes traitées");
        majStats();
    }

    @FXML
    void actionListeSelectionCommande(MouseEvent e) {
        String sel = listeCommandes.getSelectionModel().getSelectedItem();
        commandeSelectionnee = systeme.commandesDejaTraitees().stream()
                .filter(c -> c.toString().equals(sel))
                .findFirst()
                .orElse(null);
        if (commandeSelectionnee != null) {
            entreeBeneficeCommande.setText(String.format("%.2f", systeme.beneficeCommandes(commandeSelectionnee)));
        }
    }

    @FXML
    void actionSelectionClient(ActionEvent e) {
        String sel = comboBoxClients.getValue();
        clientSelectionne = systeme.ensembleClients().stream()
                .filter(c -> c.toString().equals(sel))
                .findFirst()
                .orElse(null);
        if (clientSelectionne != null) {
            entreeNbPizzasClient.setText(String.valueOf(systeme.nombrePizzasCommandeesParClient().getOrDefault(clientSelectionne, 0)));
            entreeBeneficeClient.setText(String.format("%.2f", systeme.beneficeParClient().getOrDefault(clientSelectionne, 0.0)));
        }
    }

    @FXML
    void actionBoutonAfficherListeTrieePizzas(ActionEvent e) {
        labelListePizzas.setText("Classement des pizzas");
        listePizzas.getItems().setAll(systeme.classementPizzasParNombreCommandes().stream().map(Pizza::getNom).toList());
    }

    @FXML
    void actionMenuQuitter(ActionEvent e) {
        Platform.exit();
    }

    @FXML
    void actionMenuApropos(ActionEvent e) {
        afficherAlerte("À propos", "Application Pizza - JavaFX");
    }

    private void selectionnerPizza(Pizza p) {
        entreeNomPizza.setText(p.getNom());

        double prixMinimal = systeme.calculerPrixMinimalPizza(p);
        double prixVente = systeme.getPrixPizza(p);

        entreePrixMinimalPizza.setText(String.format("%.2f", prixMinimal));
        entreePrixVentePizza.setText(String.format("%.2f", prixVente));

        double beneficeUnitaire = systeme.beneficeParPizza()
                .getOrDefault(p, 0.0);

        int nbCommandes = systeme.nombrePizzasCommandees(p);

        entreeBeneficeUnitairePizza.setText(String.format("%.2f", beneficeUnitaire));
        entreeNbCommandesPizza.setText(String.valueOf(nbCommandes));
        entreeBeneficeTotalPizza.setText(
                String.format("%.2f", beneficeUnitaire * nbCommandes)
        );

        majListeIngredients(p.getIngredients(), "Ingrédients de la pizza");
    }

    private void rafraichirTout() {
        actionBoutonAfficherToutesPizzas(null);
        comboBoxClients.getItems().setAll(systeme.ensembleClients().stream().map(Object::toString).toList());
        majStats();
    }

    private void majStats() {
        entreeNombreTotalCommandes.setText(String.valueOf(systeme.commandesDejaTraitees().size()));
        entreeBeneficeTotalCommandes.setText(String.format("%.2f", systeme.beneficeToutesCommandes()));
    }

    private void majListeIngredients(Set<Ingredient> set, String label) {
        labelListeIngredients.setText(label);
        listeIngredients.getItems().setAll(set.stream().map(Ingredient::getNom).toList());
    }

    private void majListeCommandes(List<Commande> cmds, String label) {
        labelListeCommandes.setText(label);
        listeCommandes.getItems().setAll(cmds.stream().map(Commande::toString).toList());
    }

    private void afficherAlerte(String t, String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(t);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
    @FXML
    void actionMenuSauvegarder(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Sauvegarder la pizzeria");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier pizzeria", "*.pizza")
        );

        File f = fc.showSaveDialog(null);
        if (f == null) return;

        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(systeme);
            afficherAlerte("Succès", "Sauvegarde effectuée");
        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de sauvegarder");
        }
    }

    private void rafraichirToutesListes() {
        labelListePizzas.setText("Toutes les pizzas");
        listePizzas.getItems().setAll(
                systeme.getPizzas().stream()
                        .map(Pizza::getNom)
                        .toList()
        );

        labelListeIngredients.setText("Tous les ingrédients");
        listeIngredients.getItems().setAll(
                systeme.getTousIngredients().stream()
                        .map(Ingredient::getNom)
                        .toList()
        );

        labelListeCommandes.setText("Commandes");
        listeCommandes.getItems().clear();

        comboBoxClients.getItems().setAll(
                systeme.ensembleClients().stream()
                        .map(InformationPersonnelle::toString)
                        .toList()
        );

        entreeNombreTotalCommandes.setText(
                String.valueOf(systeme.commandesDejaTraitees().size())
        );
        entreeBeneficeTotalCommandes.setText(
                String.format("%.2f", systeme.beneficeToutesCommandes())
        );
    }

    @FXML
    void actionMenuCharger(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Charger une pizzeria");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichier pizzeria", "*.pizza")
        );

        File f = fc.showOpenDialog(null);
        if (f == null) return;

        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(f))) {

            Pizzaiolo charge = (Pizzaiolo) ois.readObject();
            copierEtat(charge);

            rafraichirToutesListes();
            afficherAlerte("Succès", "Chargement effectué");

        } catch (Exception e) {
            afficherAlerte("Erreur", "Impossible de charger");
        }
    }
    private void copierEtat(Pizzaiolo autre) {
        try {
            java.lang.reflect.Field[] champs = Pizzaiolo.class.getDeclaredFields();
            for (var f : champs) {
                f.setAccessible(true);
                f.set(systeme, f.get(autre));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void actionBoutonCommandesTraiteesClient(ActionEvent event) {
        if (clientSelectionne == null) {
            afficherAlerte("Erreur", "Veuillez sélectionner un client");
            return;
        }

        List<Commande> cmds = systeme.commandesTraiteesClient(clientSelectionne);
        if (cmds == null) {
            afficherAlerte("Erreur", "Aucune commande pour ce client");
            return;
        }

        majListeCommandes(cmds, "Commandes traitées du client");
    }



}
