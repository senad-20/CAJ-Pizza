package pizzas;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Commande passée par un client
 * Une commande est définie par sa date, ses pizzas, client et son statut (Crée, validée, traitée)
 */
public class Commande {
    /**
     * Date de commande
     */
    private final LocalDateTime date;

    /**
     * Liste des pizzas de la commande
     */
    private List<Pizza> pizzas;

    /**
     * Client qui a passé la commande
     */
    private final InformationPersonnelle client;

    /**
     * Statut de la commande
     */
    private StatutCommande statut;

    public Commande(List<Pizza> pizzas, InformationPersonnelle client) {
        this.date = LocalDateTime.now();
        this.pizzas = pizzas;
        this.client = client;
        this.statut = StatutCommande.CREEE;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public List<Pizza> getPizzas() {
        return pizzas;
    }

    public InformationPersonnelle getClient() {
        return client;
    }
    public StatutCommande getStatut() {
        return statut;
    }
    public void setStatut(StatutCommande statut) {
        if(this.statut.equals(StatutCommande.TRAITEE)) throw new IllegalArgumentException("Le statut de cette commande ne peut pas être modifié");
        if(this.statut.equals(StatutCommande.VALIDEE) && statut.equals(StatutCommande.CREEE)) throw new IllegalArgumentException("Une commande validée ne peut pas changer de statut à crée");
        this.statut = statut;
    }

    public void ajouterPizza(Pizza p) {
        if(!(this.statut.equals(StatutCommande.CREEE))) throw new IllegalArgumentException("Cette commande ne peut pas etre modifie");
        this.pizzas.add(p);
    }

    public void retirerPizza(Pizza p) {
        if(!(this.statut.equals(StatutCommande.CREEE))) throw new IllegalArgumentException("Cette commande ne peut pas etre modifie");
        this.pizzas.remove(p);
    }

}
