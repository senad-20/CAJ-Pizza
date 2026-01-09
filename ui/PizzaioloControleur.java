package ui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import pizzas.*;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PizzaioloControleur {

    private final Pizzaiolo systeme = new Pizzaiolo();
    private Pizza pizzaSelectionnee;
    private Ingredient ingredientSelectionne;
    private InformationPersonnelle clientSelectionne;

    @FXML private ChoiceBox<TypePizza> choiceBoxTypeIngredient;
    @FXML private ChoiceBox<TypePizza> choiceBoxTypePizza;
    @FXML private TextField entreeBeneficeParClient, entreeBeneficeParPizza, entreeBeneficeTotalCommandes;
    @FXML private TextField entreeBeneficeTotalToutesCommandes, entreeNbCommandesClient, entreeNbCommandesPizza;
    @FXML private TextField entreeNbTotalCommandes, entreeNomIngredient, entreeNomPizza, entreePhotoPizza;
    @FXML private TextField entreePrixIngredient, entreePrixMinimalPizza, entreePrixVentePizza;
    @FXML private Label labelListeCommandes, labelListeIngredients, labelListePizzas;
    @FXML private ListView<String> listeClients, listeCommandes, listeIngredients, listePizzas;

    @FXML
    void initialize() {
        choiceBoxTypeIngredient.getItems().setAll(TypePizza.values());
        choiceBoxTypePizza.getItems().setAll(TypePizza.values());
        
        rafraichirToutesListes();
    }

    // --- 2.1 PARTIE INGRÉDIENTS ---
    @FXML
    void actionBoutonAfficherTousIngredients(ActionEvent event) {
        majListeIngredients(systeme.getTousIngredients(), "Tous les ingrédients");
    }

    @FXML
    void actionBoutonCreerIngredient(ActionEvent event) {
        try {
            String nom = entreeNomIngredient.getText();
            double prix = Double.parseDouble(entreePrixIngredient.getText());
            int res = systeme.creerIngredient(nom, prix);
            if (res == 0) actionBoutonAfficherTousIngredients(null);
            else afficherAlerte("Erreur", "Ingrédient déjà existant ou données invalides.");
        } catch (Exception e) { afficherAlerte("Erreur", "Prix invalide."); }
    }

    @FXML
    void actionBoutonInterdireType(ActionEvent event) {
        if (ingredientSelectionne != null && choiceBoxTypeIngredient.getValue() != null) {
            systeme.interdireIngredient(ingredientSelectionne.getNom(), choiceBoxTypeIngredient.getValue());
            afficherAlerte("Succès", "Ingrédient interdit pour ce type.");
        }
    }

    @FXML
    void actionSelectionIngredient(MouseEvent event) {
        String nom = listeIngredients.getSelectionModel().getSelectedItem();
        ingredientSelectionne = systeme.getTousIngredients().stream()
                .filter(i -> i.getNom().equals(nom)).findFirst().orElse(null);
        if (ingredientSelectionne != null) {
            entreeNomIngredient.setText(ingredientSelectionne.getNom());
            entreePrixIngredient.setText(String.valueOf(ingredientSelectionne.getPrix()));
        }
    }

    // --- 2.2 PARTIE PIZZAS ---
    @FXML
    void actionBoutonCreerNouvellePizza(ActionEvent event) {
        Pizza p = systeme.creerPizza(entreeNomPizza.getText(), choiceBoxTypePizza.getValue());
        if (p != null) rafraichirToutesListes();
        else afficherAlerte("Erreur", "Nom déjà pris ou type non sélectionné.");
    }

    @FXML
    void actionBoutonAjouterIngredientPizza(ActionEvent event) {
        if (pizzaSelectionnee != null && ingredientSelectionne != null) {
            int res = systeme.ajouterIngredientPizza(pizzaSelectionnee, ingredientSelectionne.getNom());
            if (res == -3) afficherAlerte("Erreur", "Ingrédient interdit pour ce type de pizza !");
            else if (res < 0) afficherAlerte("Erreur", "Erreur lors de l'ajout.");
            else selectionnerPizza(pizzaSelectionnee);
        }
    }

    @FXML
    void actionBoutonParcourirPhoto(ActionEvent event) {
        if (pizzaSelectionnee == null) return;
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.gif"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            try {
                systeme.ajouterPhoto(pizzaSelectionnee, file.getAbsolutePath());
                entreePhotoPizza.setText(file.getName());
            } catch (Exception e) { afficherAlerte("Erreur", "Impossible de charger l'image."); }
        }
    }

    @FXML
    void actionSelectionPizza(MouseEvent event) {
        String nom = listePizzas.getSelectionModel().getSelectedItem();
        pizzaSelectionnee = systeme.getPizzas().stream()
                .filter(p -> p.getNom().equals(nom)).findFirst().orElse(null);
        if (pizzaSelectionnee != null) selectionnerPizza(pizzaSelectionnee);
    }

    private void selectionnerPizza(Pizza p) {
        entreeNomPizza.setText(p.getNom());
        entreePrixVentePizza.setText(String.valueOf(systeme.getPrixPizza(p)));
        entreePrixMinimalPizza.setText(String.valueOf(systeme.calculerPrixMinimalPizza(p)));
        entreeNbCommandesPizza.setText(String.valueOf(systeme.nombrePizzasCommandees(p)));
        
        majListeIngredients(p.getIngredients(), "Ingrédients de " + p.getNom());
    }

    // --- 2.3 PARTIE COMMANDES & STATS ---
    @FXML
    void actionBoutonCommandesNonTraitees(ActionEvent event) {
        List<Commande> nonTraitees = systeme.commandeNonTraitees();
        majListeCommandes(nonTraitees, "Nouvelles commandes validées (marquées comme traitées)");
        majStatistiquesGlobales();
    }

    @FXML
    void actionBoutonClassementPizzas(ActionEvent event) {
        List<Pizza> classement = systeme.classementPizzasParNombreCommandes();
        labelListePizzas.setText("Pizzas triées par succès");
        listePizzas.getItems().setAll(classement.stream().map(Pizza::getNom).collect(Collectors.toList()));
    }

    @FXML
    void actionSelectionClient(MouseEvent event) {
        String desc = listeClients.getSelectionModel().getSelectedItem();
        clientSelectionne = systeme.ensembleClients().stream()
                .filter(c -> c.toString().equals(desc)).findFirst().orElse(null);
        if (clientSelectionne != null) {
            Map<InformationPersonnelle, Integer> nbPizzas = systeme.nombrePizzasCommandeesParClient();
            entreeNbCommandesClient.setText(String.valueOf(nbPizzas.getOrDefault(clientSelectionne, 0)));
            entreeBeneficeParClient.setText(String.format("%.2f", systeme.beneficeParClient().getOrDefault(clientSelectionne, 0.0)));
        }
    }

    // --- 2.4 MENU ---
    @FXML
    void actionMenuQuitter(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void actionMenuAPropos(ActionEvent event) {
        afficherAlerte("A propos", "Cariou's Pizza - L'application préférée des gourmets !");
    }

    // --- OUTILS ---
    private void rafraichirToutesListes() {
        listePizzas.getItems().setAll(systeme.getPizzas().stream().map(Pizza::getNom).collect(Collectors.toList()));
        listeClients.getItems().setAll(systeme.ensembleClients().stream().map(InformationPersonnelle::toString).collect(Collectors.toList()));
        majStatistiquesGlobales();
    }

    private void majStatistiquesGlobales() {
        entreeNbTotalCommandes.setText(String.valueOf(systeme.commandesDejaTraitees().size()));
        entreeBeneficeTotalToutesCommandes.setText(String.format("%.2f", systeme.beneficeToutesCommandes()));
    }

    private void majListeIngredients(Set<Ingredient> ings, String label) {
        labelListeIngredients.setText(label);
        listeIngredients.getItems().setAll(ings.stream().map(Ingredient::getNom).collect(Collectors.toList()));
    }

    private void majListeCommandes(List<Commande> cmds, String label) {
        labelListeCommandes.setText(label);
        listeCommandes.getItems().setAll(cmds.stream().map(Commande::toString).collect(Collectors.toList()));
    }

    private void afficherAlerte(String titre, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
