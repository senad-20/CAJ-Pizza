package pizzas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une commande passée par un client.
 * Une commande est définie par sa date, la liste des pizzas sélectionnées,
 * le client correspondant, et son statut (créée, validée, traitée).
 */
public class Commande {

    /**
     * Date et heure de création de la commande.
     */
    private final LocalDateTime date;

    /**
     * Liste interne des pizzas de la commande.
     * Une copie défensive est effectuée dans le constructeur.
     */
    private final List<Pizza> pizzas = new ArrayList<>();

    /**
     * Informations personnelles du client ayant passé la commande.
     */
    private final InformationPersonnelle client;

    /**
     * Statut actuel de la commande (créée, validée ou traitée).
     */
    private StatutCommande statut;

    /**
     * Construit une nouvelle commande.
     *
     * @param pizzas liste des pizzas constituant la commande
     * @param client informations personnelles du client
     * @throws CommandeException si la liste de pizzas est nulle, vide,
     *                           si elle contient un élément nul,
     *                           ou si le client est nul
     */
    public Commande(List<Pizza> pizzas, InformationPersonnelle client) {
        if (pizzas == null) {
            throw new CommandeException("La liste des pizzas ne peut pas etre null");
        }
        if (client == null) {
            throw new CommandeException("Le client ne peut pas etre null");
        }

        this.date = LocalDateTime.now();

        for (Pizza pizza : pizzas) {
            if (pizza == null) {
                throw new CommandeException("La liste de pizzas contient une pizza nulle.");
            }
            this.pizzas.add(pizza);
        }

        this.client = client;
        this.statut = StatutCommande.CREE;
    }

    /**
     * Retourne la date de création de la commande.
     *
     * @return la date de la commande
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Retourne une copie immuable de la liste des pizzas de la commande.
     *
     * @return une copie immuable de la liste des pizzas
     */
    public List<Pizza> getPizzas() {
        return List.copyOf(pizzas);
    }

    /**
     * Retourne les informations personnelles du client.
     *
     * @return le client ayant passé la commande
     */
    public InformationPersonnelle getClient() {
        return client;
    }

    /**
     * Retourne le statut actuel de la commande.
     *
     * @return le statut de la commande
     */
    public StatutCommande getStatut() {
        return statut;
    }

    /**
     * Met à jour le statut de la commande.
     *
     * Règles :
     * <ul>
     *     <li>Une commande traitée ne peut plus changer de statut.</li>
     *     <li>Une commande validée ne peut pas revenir à l'état créé.</li>
     * </ul>
     *
     * @param statut nouveau statut souhaité
     * @throws CommandeException si la transition de statut est interdite
     */
    public void setStatut(StatutCommande statut) {
        if (this.statut.equals(StatutCommande.TRAITEE))
            throw new CommandeException("Le statut de cette commande ne peut pas être modifié");

        if (this.statut.equals(StatutCommande.VALIDEE) && statut.equals(StatutCommande.CREE))
            throw new CommandeException("Une commande validée ne peut pas changer de statut à crée");

        this.statut = statut;
    }

    /**
     * Ajoute une pizza à la commande.
     *
     * Conditions :
     * <ul>
     *     <li>La commande doit être au statut 'créée'.</li>
     *     <li>La pizza ajoutée ne doit pas être nulle.</li>
     * </ul>
     *
     * @param p pizza à ajouter
     * @throws CommandeException si la commande ne peut plus être modifiée
     *                           ou si la pizza est nulle
     */
    public void ajouterPizza(Pizza p) {
        if (!(this.statut.equals(StatutCommande.CREE)))
            throw new CommandeException("Cette commande ne peut pas être modifie");

        if (p == null) {
            throw new CommandeException("Impossible d'ajouter une pizza nulle.");
        }

        this.pizzas.add(p);
    }

    /**
     * Retire une pizza de la commande.
     *
     * Conditions :
     * <ul>
     *     <li>La commande doit être au statut 'créée'.</li>
     *     <li>La pizza doit exister dans la commande.</li>
     * </ul>
     *
     * @param p pizza à retirer
     * @throws CommandeException si la commande ne peut plus être modifiée
     *                           ou si la pizza n'existe pas dans la liste
     */
    public void retirerPizza(Pizza p) {
        if (!(this.statut.equals(StatutCommande.CREE)))
            throw new CommandeException("Cette commande ne peut pas etre modifie");

        if (!pizzas.remove(p)) {
            throw new CommandeException("La pizza à retirer n'existe pas dans la commande.");
        }
    }

    /**
     * Retourne une représentation textuelle de la commande.
     *
     * @return une chaîne contenant la date, le client, le statut et
     *         le nombre de pizzas.
     */
    @Override
    public String toString() {
        return "Commande{" +
                "date=" + date +
                ", client=" + client +
                ", statut=" + statut +
                ", nbPizzas=" + pizzas.size() +
                '}';
    }

    /**
     * Indique si deux commandes sont égales.
     * Deux commandes sont considérées égales si :
     * <ul>
     *     <li>elles ont la même date,</li>
     *     <li>le même client,</li>
     *     <li>le même statut,</li>
     *     <li>et la même liste de pizzas.</li>
     * </ul>
     *
     * @param o objet à comparer
     * @return true si les commandes sont égales, false sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commande)) return false;

        Commande c = (Commande) o;
        return date.equals(c.date)
                && client.equals(c.client)
                && statut.equals(c.statut)
                && pizzas.equals(c.pizzas);
    }

    /**
     * Calcule le hashcode de la commande.
     * Doit être cohérent avec equals().
     *
     * @return un entier représentant le hashcode
     */
    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + client.hashCode();
        result = 31 * result + statut.hashCode();
        result = 31 * result + pizzas.hashCode();
        return result;
    }


}
