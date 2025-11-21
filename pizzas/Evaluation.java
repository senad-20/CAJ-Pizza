package pizzas;

import java.util.Objects;

/**
 * Évaluation laissée par un client sur une pizza.
 * Une évaluation est définie par un auteur, une note comprise entre 0 et 5
 * et un commentaire optionnel.
 */
public class Evaluation {

    /**
     * Auteur de l'évaluation.
     */
    private final InformationPersonnelle auteur;

    /**
     * Note attribuée à la pizza (entre 0 et 5 inclus).
     */
    private final double note;

    /**
     * Commentaire associé à l'évaluation, éventuellement {@code null}.
     */
    private final String commentaire;


    /**
     * Crée une évaluation avec un commentaire.
     *
     * @param auteur l'auteur de l'évaluation
     * @param note la note attribuée (entre 0 et 5 inclus)
     * @param commentaire un commentaire non vide
     * @throws IllegalArgumentException si la note est invalide ou si le commentaire est vide
     */
    public Evaluation(InformationPersonnelle auteur, double note, String commentaire) {
        if (note < 0 || note > 5) {
            throw new IllegalArgumentException(
                    "Une note doit être comprise entre 0 et 5"
            );
        }
        this.auteur = auteur;
        this.note = note;
        this.commentaire = commentaire;
    }

    /**
     * Retourne l'auteur de l'évaluation.
     *
     * @return l'auteur ayant rédigé l'évaluation
     */
    public InformationPersonnelle getAuteur() {
        return auteur;
    }

    /**
     * Retourne la note attribuée.
     *
     * @return la note de l'évaluation
     */
    public double getNote() {
        return note;
    }

    /**
     * Retourne le commentaire de l'évaluation.
     *
     * @return le commentaire ou {@code null} s'il n'existe pas
     */
    public String getCommentaire() {
        return commentaire;
    }

    /**
     * Retourne une représentation textuelle de l'évaluation.
     *
     * @return une chaîne contenant la note et éventuellement le commentaire
     */
    @Override
    public String toString() {
        if (commentaire == null) {
            return "Evaluation{note=" + note + "}";
        }
        return "Evaluation{note=" + note + ", commentaire='"
                + commentaire + "'}";
    }

    /**
     * Indique si deux évaluations sont égales.
     * Deux évaluations sont égales si elles ont :
     * <ul>
     *     <li>le même auteur,</li>
     *     <li>la même note,</li>
     *     <li>le même commentaire.</li>
     * </ul>
     *
     * @param obj l'objet à comparer
     * @return {@code true} si les évaluations sont égales,
     *         {@code false} sinon
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Evaluation)) {
            return false;
        }
        Evaluation other = (Evaluation) obj;
        return Double.compare(other.note, note) == 0
                && Objects.equals(auteur, other.auteur)
                && Objects.equals(commentaire, other.commentaire);
    }

    /**
     * Calcule le hashcode de l'évaluation.
     * Le hashcode est cohérent avec la méthode {@link #equals(Object)}.
     *
     * @return le hashcode de cette évaluation
     */
    @Override
    public int hashCode() {
        return Objects.hash(auteur, note, commentaire);
    }
}
