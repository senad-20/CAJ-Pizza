package pizzas;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Représente une pizza.
 * Une pizza est définie par un nom, un type, un ensemble d'ingrédients,
 * un prix fixé (éventuellement non initialisé) et une liste d'évaluations.
 */
public class Pizza {

    /**
     * Nom de la pizza.
     */
    private final String nom;

    /**
     * Type de la pizza.
     */
    private final TypePizza typepizza;

    /**
     * Ensemble des ingrédients composant la pizza.
     */
    private final Set<Ingredient> ingredients;

    /**
     * Prix fixé manuellement pour la pizza.
     * La valeur -1 indique qu'aucun prix n'a encore été fixé.
     */
    private double prixfixe;

    /**
     * Liste des évaluations données à la pizza.
     */
    private final List<Evaluation> evaluations;

    /**
     * Chemin vers une photo représentant la pizza.
     */
    private String photo;

    /**
     * Crée une nouvelle pizza.
     *
     * @param nom        le nom non vide de la pizza
     * @param typepizza  le type de la pizza
     * @throws IllegalArgumentException si le nom est vide ou invalide
     */
    public Pizza(String nom, TypePizza typepizza) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est invalide");
        } else {
            this.nom = nom;
        }
        this.typepizza = typepizza;
        this.ingredients = new HashSet<>();
        this.prixfixe = -1;
        this.photo = null;
        this.evaluations = new ArrayList<>();
    }

    /**
     * Retourne le nom de la pizza.
     *
     * @return le nom de la pizza
     */
    public String getNom() {
        return this.nom;
    }

    /**
     * Retourne le type de la pizza.
     *
     * @return le type de la pizza
     */
    public TypePizza getTypePizza() {
        return this.typepizza;
    }

    /**
     * Retourne l'ensemble des ingrédients de la pizza.
     *
     * @return l'ensemble des ingrédients
     */
    public Set<Ingredient> getIngredients() {
        return this.ingredients;
    }

    /**
     * Retourne le prix fixé de la pizza.
     * Une valeur de -1 indique qu'aucun prix manuel n'a été défini.
     *
     * @return le prix fixé de la pizza
     */
    public double getPrixfixe() {
        return this.prixfixe;
    }

    /**
     * Retourne la liste des évaluations associées à la pizza.
     *
     * @return la liste des évaluations
     */
    public List<Evaluation> getEvaluations() {
        return this.evaluations;
    }

    /**
     * Ajoute un ingrédient à la pizza.
     * Ne lance aucune exception si l'ingrédient est déjà présent.
     *
     * @param ingredient l'ingrédient à ajouter
     */
    public void ajouterIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
    }

    /**
     * Retire un ingrédient de la pizza.
     * Ne lance aucune exception si l'ingrédient n'était pas présent.
     *
     * @param ingredient l'ingrédient à retirer
     */
    public void enleverIngredient(Ingredient ingredient) {
        this.ingredients.remove(ingredient);
    }

    /**
     * Ajoute une nouvelle évaluation à la pizza.
     *
     * @param e l'évaluation à ajouter
     */
    public void ajouternote(Evaluation e) {
        this.evaluations.add(e);
    }

    /**
     * Modifie le prix fixé de la pizza.
     *
     * @param prixfixe le nouveau prix fixé
     */
    public void setPrixfixe(double prixfixe) {
        this.prixfixe = prixfixe;
    }

    /**
     * Modifie la photo associée à la pizza.
     *
     * @param photo le chemin du fichier de la photo
     */
    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Retourne une représentation textuelle de la pizza.
     *
     * @return une chaîne représentant la pizza
     */
    @Override
    public String toString() {
        return "Pizza{" + nom + ", type=" + typepizza + "}";
    }

    /**
     * Compare cette pizza avec un autre objet.
     * Deux pizzas sont considérées comme égales si elles portent le même nom.
     *
     * @param obj l'objet à comparer
     * @return {@code true} si les deux pizzas ont le même nom,
     *         {@code false} sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Pizza other = (Pizza) obj;
        return this.nom.equals(other.nom);
    }

    /**
     * Retourne le code de hachage de la pizza.
     * Le code de hachage est basé uniquement sur le nom.
     *
     * @return le code de hachage de cette pizza
     */
    @Override
    public int hashCode() {
        return nom.hashCode();
    }

}
