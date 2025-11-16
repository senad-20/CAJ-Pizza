package pizzas;

/**
 * Représente les différents statuts possibles d'une commande.
 * Une commande peut être :
 * <ul>
 *   <li>{@code CREEE} : la commande vient d'être créée par le client et peut encore être modifiée</li>
 *   <li>{@code VALIDEE} : la commande a été validée par le client et ne peut plus être modifiée</li>
 *   <li>{@code TRAITEE} : la commande a été traitée par le pizzaïolo</li>
 * </ul>
 */
public enum StatutCommande {

    /**
     * Commande créée par le client (modifiable).
     */
    CREEE,

    /**
     * Commande validée par le client (non modifiable).
     */
    VALIDEE,

    /**
     * Commande traitée par le pizzaïolo (non modifiable).
     */
    TRAITEE
}
