package pizzas;

/**
 * Évaluation laissée par un client sur une pizza.
 * Une évaluation est définie par une note entre 0 et 5 et un commentaire textuel optionnel.
 */
public class Evaluation {

    /**
     * Note de l'évaluation (comprise entre 0 et 5).
     */
    private final double note;

    /**
     * Commentaire optionnel..
     */
    private final String commentaire;

    /**
     * Crée une évaluation sans commentaire.
     *
     * @param note la note attribuée (entière ou décimale) entre 0 et 5 inclus
     * @throws IllegalArgumentException si la note n'est pas comprise entre 0 et 5
     */
    public Evaluation(double note) {
        if (note >= 0 && note <= 5) {
            this.note = note;
            this.commentaire = null;
        } else {
            throw new IllegalArgumentException("Une note doit être comprise entre 0 et 5");
        }
    }

    /**
     * Crée une évaluation avec un commentaire.
     *
     * @param note la note attribuée (entre 0 et 5 inclus)
     * @param commentaire un commentaire non nul et non vide
     * @throws IllegalArgumentException si la note n'est pas valide ou si le commentaire est invalide
     */
    public Evaluation(double note, String commentaire) {
        if (note < 0 || note > 5) {
            throw new IllegalArgumentException("Une note doit être comprise entre 0 et 5");
        }
        if (commentaire == null || commentaire.trim().isEmpty()) {
            throw new IllegalArgumentException("Le commentaire est invalide");
        }
        this.note = note;
        this.commentaire = commentaire;
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
     * Peut être {@code null} si l'évaluation n'a pas de commentaire.
     *
     * @return le commentaire ou {@code null}
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
        return "Evaluation{note=" + note + ", commentaire='" + commentaire + "'}";
    }
}
