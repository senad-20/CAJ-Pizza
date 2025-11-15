package pizzas;

/**
 * Représente un ingrédient utilisé dans la création d'une pizza.
 * Un ingrédient est défini par un nom non vide et un prix non négatif.
 */
public class Ingredient {

    /**
     * Nom de l'ingrédient.
     */
    private String nom;

    /**
     * Prix de l'ingrédient (>= 0).
     */
    private double prix;

    /**
     * Crée un nouvel ingrédient.
     *
     * @param nom le nom de l'ingrédient (non null et non vide)
     * @param prix le prix de l'ingrédient (doit être >= 0)
     * @throws IllegalArgumentException si le nom est vide ou si le prix est négatif
     */
    public Ingredient(String nom, double prix) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'ingrédient est invalide.");
        }
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix d'un ingrédient ne peut pas être négatif.");
        }
        this.nom = nom;
        this.prix = prix;
    }

    /**
     * Retourne le nom de l'ingrédient.
     *
     * @return le nom de l'ingrédient
     */
    public String getNom() {
        return nom;
    }

    /**
     * Retourne le prix de l'ingrédient.
     *
     * @return le prix de l'ingrédient
     */
    public double getPrix() {
        return prix;
    }

    /**
     * Modifie le nom de l'ingrédient.
     *
     * @param nom le nouveau nom (non null et non vide)
     * @throws IllegalArgumentException si le nom est vide
     */
    public void setNom(String nom) {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau nom est invalide.");
        }
        this.nom = nom;
    }

    /**
     * Modifie le prix de l'ingrédient.
     *
     * @param prix le nouveau prix (>= 0)
     * @throws IllegalArgumentException si le prix est négatif
     */
    public void setPrix(double prix) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix d'un ingrédient ne peut pas être négatif.");
        }
        this.prix = prix;
    }
}
