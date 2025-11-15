package pizzas;

import java.util.HashSet;
import java.util.Set;

/**
 * Représente une pizza
 * Une pizza est définie par un nom, type, ingrédients, prix minimal, prix fixé et note moyenne
 */
public class Pizza {
    /**
     * Nom de la Pizza
     */
    private String nom;

    /**
     * Type de pizza
     */
    private TypePizza typepizza;

    /**
     * Liste d'ingrédients
     */
    private Set<Ingredient> ingredients = new HashSet<>();

    /**
     * Prix minmal de la pizza
     */
    private double prixmin;

    /**
     * Prix fixé de la pizza
     */
    private double prixfixe;

    /**
     * Note moyenne de la pizza
     */
    private double notemoyenne;

    /**
     * Création d'une nouvelle pizza
     *
     * @param nom         Le nom non-vide de la pizza
     * @param typepizza   Type de la pizza
     */
    public Pizza(String nom, TypePizza typepizza) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom est invalide");
        } else this.nom = nom;
        this.typepizza = typepizza;
        this.ingredients = ingredients;
        double sum = ingredients.stream()
                .mapToDouble(Ingredient::getPrix)
                .sum();
        sum *= 1.4;
        this.prixmin = Math.ceil(sum * 10) / 10.0;
        if (prixfixe < this.prixmin) {
            throw new IllegalArgumentException("Le prix est inferieur au prix minimal");
        }
        else this.prixfixe=prixfixe;

    }
}