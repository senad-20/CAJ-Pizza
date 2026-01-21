package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import pizzas.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import java.util.*;
import java.util.stream.Collectors;

public class ClientControleur {

  private final Pizzaiolo systeme = MainInterface.SYSTEME;
  private Commande commandeSelectionnee;
  private Pizza pizzaSelectionnee;

  @FXML private ChoiceBox<String> choiceBoxFiltreType;
  @FXML private TextField entreeAdresseClient;
  @FXML private TextField entreeAgeClient;
  @FXML private TextField entreeAuteurEvaluation;
  @FXML private TextField entreeEmailClient;
  @FXML private TextField entreeEvaluationMoyenneEvaluations;
  @FXML private TextField entreeFiltreContientIngredient;
  @FXML private TextField entreeFiltrePrixMax;
  @FXML private TextField entreeMotDePasseClient;
  @FXML private TextField entreeNomClient;
  @FXML private TextField entreeNomPizza;
  @FXML private TextField entreeNomPizzaEvaluee;
  @FXML private TextField entreeNoteMoyennePizza;
  @FXML private TextField entreePrenomClient;
  @FXML private TextField entreePrixPizza;
  @FXML private TextField entreeTypePizza;
  @FXML private Label labelListeCommandes;
  @FXML private Label labelListePizzas;
  @FXML private ListView<String> listeCommandes;
  @FXML private ListView<String> listeEvaluations;
  @FXML private ListView<String> listeIngredients;
  @FXML private ListView<String> listePizzas;
  @FXML private ChoiceBox<Integer> choiceBoxNoteEvaluation;
  @FXML private StackPane panePhotoPizza;
  @FXML private TextArea texteCommentaireEvaluation;

  @FXML
  void initialize() {
    for (TypePizza t : TypePizza.values()) {
      choiceBoxFiltreType.getItems().add(t.name());
    }
    for (int i = 0; i <= 5; i++) {
      choiceBoxNoteEvaluation.getItems().add(i);
    }
    actionBoutonAfficherToutesPizzas(null);
  }

  @FXML
  void actionBoutonInscription(ActionEvent event) {
    try {
      InformationPersonnelle info = new InformationPersonnelle(
              entreeNomClient.getText(),
              entreePrenomClient.getText(),
              entreeAdresseClient.getText(),
              Integer.parseInt(entreeAgeClient.getText())
      );
      int res = systeme.inscription(entreeEmailClient.getText(), entreeMotDePasseClient.getText(), info);
      if (res == 0) {
        afficherAlerte("Succès", "Inscription réussie");
      } else {
        afficherAlerte("Erreur", "Email déjà utilisé ou données invalides");
      }
    } catch (Exception e) {
      afficherAlerte("Erreur", "Champs invalides");
    }
  }

  @FXML
  void actionBoutonConnexion(ActionEvent event) {
    if (systeme.connexion(entreeEmailClient.getText(), entreeMotDePasseClient.getText())) {
      try {
        // Cast the object to the correct type (adjust 'Client' to your actual class name)
        Pizzaiolo.Client client = (Pizzaiolo.Client) systeme.getClientConnecte();
        
        if (client != null) {
          InformationPersonnelle info = client.getInfo();
          entreeNomClient.setText(info.getNom());
          entreePrenomClient.setText(info.getPrenom());
          entreeAdresseClient.setText(info.getAdresse());
          entreeAgeClient.setText(String.valueOf(info.getAge()));
        }
      } catch (ClassCastException e) {
          afficherAlerte("Erreur", "Le type de compte est invalide.");
      } catch (Exception e) {
          // Log the error for the developer
          e.printStackTrace(); 
          afficherAlerte("Erreur", "Impossible de charger les données du client.");
      }
    } else {
      afficherAlerte("Erreur", "Identifiants incorrects");
    }
  }

  @FXML
  void actionBoutonDeconnexion(ActionEvent event) {
    try {
      systeme.deconnexion();
      entreeNomClient.clear();
      entreePrenomClient.clear();
      entreeAdresseClient.clear();
      entreeAgeClient.clear();
      entreeEmailClient.clear();
      entreeMotDePasseClient.clear();
      commandeSelectionnee = null;
    } catch (NonConnecteException e) {
      afficherAlerte("Erreur", "Aucun client connecté");
    }
  }

  @FXML
  void actionBoutonAfficherToutesPizzas(ActionEvent event) {
    systeme.supprimerFiltres();
    majListePizzas(systeme.getPizzas(), "Toutes les pizzas");
  }

  @FXML
  void actionSelectionPizza(MouseEvent event) {
    String nom = listePizzas.getSelectionModel().getSelectedItem();
    if (nom == null) return;

    pizzaSelectionnee = systeme.getPizzas().stream()
            .filter(p -> p.getNom().equals(nom))
            .findFirst()
            .orElse(null);

    if (pizzaSelectionnee == null) return;

    entreeNomPizza.setText(pizzaSelectionnee.getNom());
    entreePrixPizza.setText(String.format("%.2f€", systeme.getPrixPizza(pizzaSelectionnee)));
    entreeTypePizza.setText(pizzaSelectionnee.getTypePizza().name());
    entreeNoteMoyennePizza.setText(String.format("%.1f", systeme.getNoteMoyenne(pizzaSelectionnee)));
    listeIngredients.getItems().setAll(
            pizzaSelectionnee.getIngredients().stream()
                    .map(Ingredient::getNom)
                    .toList()
    );
  }

  @FXML
  void actionBoutonCreerNouvelleCommande(ActionEvent event) {
    try {
      commandeSelectionnee = systeme.debuterCommande();
      majListeCommandes(systeme.getCommandesEncours(), "Commandes en cours");
      listeCommandes.getSelectionModel().select(commandeSelectionnee.toString());
    } catch (NonConnecteException e) {
      afficherAlerte("Erreur", "Connexion requise");
    }
  }

  @FXML
  void actionBoutonAjouterPizzaSelectionneeCommande(ActionEvent event) {
    try {
      if (pizzaSelectionnee == null || commandeSelectionnee == null) {
        afficherAlerte("Erreur", "Sélection invalide");
        return;
      }
      systeme.ajouterPizza(pizzaSelectionnee, 1, commandeSelectionnee);
    } catch (Exception e) {
      afficherAlerte("Erreur", e.getMessage());
    }
  }

  @FXML
  void actionBoutonValiderCommandeEnCours(ActionEvent event) {
    try {
      if (commandeSelectionnee == null) return;
      systeme.validerCommande(commandeSelectionnee);
      majListeCommandes(systeme.getCommandesEncours(), "Commandes en cours");
      commandeSelectionnee = null;
    } catch (Exception e) {
      afficherAlerte("Erreur", e.getMessage());
    }
  }

  @FXML
  void actionSelectionCommnade(MouseEvent event) {
    String selection = listeCommandes.getSelectionModel().getSelectedItem();
    if (selection == null) return;

    try {
      commandeSelectionnee = systeme.getCommandesEncours().stream()
              .filter(c -> c.toString().equals(selection))
              .findFirst()
              .orElse(null);

      if (commandeSelectionnee == null) {
        commandeSelectionnee = systeme.getCommandePassees().stream()
                .filter(c -> c.toString().equals(selection))
                .findFirst()
                .orElse(null);
      }

      if (commandeSelectionnee != null) {
        majListePizzas(new HashSet<>(commandeSelectionnee.getPizzas()), "Pizzas de la commande");
      }
    } catch (NonConnecteException ignored) {}
  }

  @FXML
  void actionBoutonAfficherCommandesEnCours(ActionEvent event) {
    try {
      majListeCommandes(systeme.getCommandesEncours(), "Commandes en cours");
    } catch (NonConnecteException e) {
      afficherAlerte("Erreur", "Connexion requise");
    }
  }

  @FXML
  void actionBoutonAfficherCommandesTtraitees(ActionEvent event) {
    try {
      majListeCommandes(systeme.getCommandePassees(), "Commandes traitées");
    } catch (NonConnecteException e) {
      afficherAlerte("Erreur", "Connexion requise");
    }
  }

  @FXML
  void actionBoutonAppliquerFiltreType(ActionEvent event) {
    String type = choiceBoxFiltreType.getValue();
    if (type == null) return;
    systeme.ajouterFiltre(TypePizza.valueOf(type));
    majListePizzas(systeme.selectionPizzaFiltres(), "Filtre type");
  }

  @FXML
  void actionBoutonAppliquerFiltreContientngredient(ActionEvent event) {
    String ingr = entreeFiltreContientIngredient.getText();
    if (ingr == null || ingr.isBlank()) return;

    systeme.ajouterFiltre(ingr);
    majListePizzas(systeme.selectionPizzaFiltres(), "Filtre ingrédient");
  }

  @FXML
  void actionBoutonAppliquerFiltrePrixMax(ActionEvent event) {
    try {
      double prix = Double.parseDouble(entreeFiltrePrixMax.getText());
      systeme.ajouterFiltre(prix);
      majListePizzas(systeme.selectionPizzaFiltres(), "Filtre prix max");
    } catch (Exception e) {
      afficherAlerte("Erreur", "Prix invalide");
    }
  }

  @FXML
  void actionBoutonReinitialiserFiltre(ActionEvent event) {
    systeme.supprimerFiltres();
    actionBoutonAfficherToutesPizzas(null);
  }

  @FXML
  void actionBoutonAfficherEvaluationPizzas(ActionEvent event) {
    if (pizzaSelectionnee == null) return;
    entreeNomPizzaEvaluee.setText(pizzaSelectionnee.getNom());
    entreeEvaluationMoyenneEvaluations.setText(String.format("%.1f", systeme.getNoteMoyenne(pizzaSelectionnee)));
    listeEvaluations.getItems().setAll(
            systeme.getEvaluationsPizza(pizzaSelectionnee).stream()
                    .map(Evaluation::toString)
                    .toList()
    );
  }

  @FXML
  void actionSelectionEvaluation(MouseEvent event) {}

  @FXML
  void actionBoutonAjouterMonEvaluation(ActionEvent event) {
    try {
      if (pizzaSelectionnee == null) return;
      Integer note = choiceBoxNoteEvaluation.getValue();
      if (note == null) return;
      boolean ok = systeme.ajouterEvaluation(pizzaSelectionnee, note, texteCommentaireEvaluation.getText());
      if (!ok) afficherAlerte("Erreur", "Évaluation refusée");
    } catch (Exception e) {
      afficherAlerte("Erreur", e.getMessage());
    }
  }

  private void majListePizzas(Set<Pizza> pizzas, String label) {
    labelListePizzas.setText(label);
    listePizzas.getItems().setAll(
            pizzas.stream().map(Pizza::getNom).collect(Collectors.toList())
    );
  }

  private void majListeCommandes(List<Commande> commandes, String label) {
    labelListeCommandes.setText(label);
    listeCommandes.getItems().setAll(
            commandes.stream().map(Commande::toString).collect(Collectors.toList())
    );
  }

  private void afficherAlerte(String titre, String message) {
    Alert a = new Alert(Alert.AlertType.INFORMATION);
    a.setTitle(titre);
    a.setHeaderText(null);
    a.setContentText(message);
    a.showAndWait();
  }
  private void afficherPhotoPizza(Pizza p) {
    panePhotoPizza.getChildren().clear();

    if (p == null || p.getPhoto() == null) return;

    File f = new File(p.getPhoto());
    if (!f.exists()) return;

    ImageView iv = new ImageView(new Image(f.toURI().toString()));
    iv.setPreserveRatio(true);
    iv.setFitWidth(180);
    iv.setFitHeight(120);

    panePhotoPizza.getChildren().add(iv);
  }

}
