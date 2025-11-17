package pizzas;

/**
 * Exception levée lorsqu'une erreur survient lors de la manipulation
 * d'une commande (création, modification, statut, etc.).
 */
public class CommandeException extends RuntimeException {

  /**
   * Identifiant de sérialisation.
   */
  private static final long serialVersionUID = -2876441299971092712L;

  /**
   * Crée une nouvelle exception de commande avec un message descriptif.
   *
   * @param message description de l'erreur
   */
  public CommandeException(String message) {
    super(message);
  }

  /**
   * Crée une nouvelle exception de commande avec un message et une cause.
   *
   * @param message description de l'erreur
   * @param cause cause originale de l'erreur
   */
  public CommandeException(String message, Throwable cause) {
    super(message, cause);
  }
}
