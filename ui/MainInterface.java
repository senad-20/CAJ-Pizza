package ui;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pizzas.*;

/**
 * Classe exécutable qui lance l'interface graphique de l'application.
 *
 * @author Eric Cariou
 */
public final class MainInterface extends Application {
  
  /**
   * Affiche la fenêtre du client pour commander les pizzas.
   */
  public static final Pizzaiolo SYSTEME = new Pizzaiolo();
  static {
    DemoDataInjector.inject(SYSTEME);
  }
  public void startFenetreClient() {
    try {
      URL url = getClass().getResource("client.fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(url);
      VBox root = (VBox) fxmlLoader.load();
      
      Scene scene = new Scene(root, 1210, 620);
      
      Stage stage = new Stage();
      stage.setResizable(true);
      stage.setTitle("Commandes de pizzas");
      
      stage.setScene(scene);
      stage.show();
      
    } catch (IOException e) {
      System.err.println("Erreur au chargement de la fenêtre du client : " + e);
    }
  }
  
  /**
   * Affiche la fenêtre de gestion des pizzas pour le pizzaïolo.
   *
   * @param primaryStage le paramètre passé par JavaFX pour la fenêtre
   *        principale
   */
  public void startFenetrePizzaiolo(Stage primaryStage) {
    try {
      URL url = getClass().getResource("pizzaiolo.fxml");
      FXMLLoader fxmlLoader = new FXMLLoader(url);
      VBox root = (VBox) fxmlLoader.load();
      
      Scene scene = new Scene(root, 985, 630);
      
      primaryStage.setScene(scene);
      primaryStage.setResizable(true);
      primaryStage.setTitle("Gestion des pizzas");
      primaryStage.show();
      
    } catch (IOException e) {
      System.err
          .println("Erreur au chargement de la fenêtre du pizzaïolo : " + e);
    }
  }
  
  @Override
  public void start(Stage primaryStage) {
    
    // Rajouter ici du code si besoin
    
    // Lancement des 2 fenêtres de l'application
    this.startFenetrePizzaiolo(primaryStage);
    this.startFenetreClient();
    
    // Rajouter ici du code si besoin
  }
  
  /**
   * Méthode principale qui lance l'application en affichant les deux fenêtres.
   *
   * @param args non utilisés ici
   */
  public static void main(String[] args) {
    launch(args);
  }


  public class DemoDataInjector {

    public static void inject(Pizzaiolo systeme) {
      try {
        // ---------------- INGREDIENTS ----------------
        systeme.creerIngredient("Tomate", 0.5);
        systeme.creerIngredient("Fromage", 1.0);
        systeme.creerIngredient("Jambon", 1.5);
        systeme.creerIngredient("Champignon", 1.2);
        systeme.creerIngredient("Olive", 0.7);

        // ---------------- PIZZAS ----------------
        Pizza margherita = systeme.creerPizza("Margherita", TypePizza.VEGETARIENNE);
        Pizza reine = systeme.creerPizza("Reine", TypePizza.REGIONALE);
        Pizza forestiere = systeme.creerPizza("Forestière", TypePizza.VIANDE);

        systeme.ajouterIngredientPizza(margherita, "Tomate");
        systeme.ajouterIngredientPizza(margherita, "Fromage");

        systeme.ajouterIngredientPizza(reine, "Tomate");
        systeme.ajouterIngredientPizza(reine, "Fromage");
        systeme.ajouterIngredientPizza(reine, "Jambon");

        systeme.ajouterIngredientPizza(forestiere, "Fromage");
        systeme.ajouterIngredientPizza(forestiere, "Champignon");
        systeme.ajouterIngredientPizza(forestiere, "Jambon");
        double min = systeme.calculerPrixMinimalPizza(reine);
        systeme.setPrixPizza(reine, min + 2.5);
        min = systeme.calculerPrixMinimalPizza(margherita);
        systeme.setPrixPizza(margherita, min + 7.5);
        min = systeme.calculerPrixMinimalPizza(forestiere);
        systeme.setPrixPizza(forestiere, min + 8.5);

        // ---------------- CLIENTS ----------------
        InformationPersonnelle c1 =
                new InformationPersonnelle("Doe", "John", "Brest", 22);
        InformationPersonnelle c2 =
                new InformationPersonnelle("Smith", "Anna", "Rennes", 25);

        systeme.inscription("john@demo.fr", "demo", c1);
        systeme.inscription("anna@demo.fr", "demo", c2);

        // ---------------- COMMANDES CLIENT 1 ----------------
        systeme.connexion("john@demo.fr", "demo");

        Commande cmd1 = systeme.debuterCommande();
        systeme.ajouterPizza(margherita, 1, cmd1);
        systeme.ajouterPizza(reine, 1, cmd1);
        systeme.validerCommande(cmd1);

        Commande cmd2 = systeme.debuterCommande();
        systeme.ajouterPizza(forestiere, 2, cmd2);
        systeme.validerCommande(cmd2);

        systeme.deconnexion();

        // ---------------- COMMANDES CLIENT 2 ----------------
        systeme.connexion("anna@demo.fr", "demo");

        Commande cmd3 = systeme.debuterCommande();
        systeme.ajouterPizza(margherita, 1, cmd3);
        systeme.validerCommande(cmd3);

        systeme.deconnexion();

        // ---------------- PIZZAÏOLO TRAITE LES COMMANDES ----------------
        for (Commande c : systeme.commandeNonTraitees()) {
          systeme.validerCommande(c);
        }

        // ---------------- EVALUATIONS ----------------
        systeme.connexion("john@demo.fr", "demo");
        systeme.ajouterEvaluation(margherita, 5, "Excellente !");
        systeme.ajouterEvaluation(reine, 4, "Très bonne");
        systeme.deconnexion();

        systeme.connexion("anna@demo.fr", "demo");
        systeme.ajouterEvaluation(margherita, 4, "Bonne pizza");
        systeme.deconnexion();

      } catch (Exception ignored) {
        // intentionally silent: demo must NEVER crash
      }
    }
  }


}
