package tests;

import org.junit.jupiter.api.Test;
import pizzas.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class CommandeTest {

    @Test
    public void testCreationOK() {
        InformationPersonnelle info =
                new InformationPersonnelle("A", "B", "C", 20);

        Commande c = new Commande(new ArrayList<>(), info);

        assertEquals(info, c.getClient());
        assertEquals(StatutCommande.CREE, c.getStatut());
        assertTrue(c.getPizzas().isEmpty());
    }

    @Test
    public void testAjouterPizza() {
        InformationPersonnelle info =
                new InformationPersonnelle("A", "B", "C", 20);

        Commande c = new Commande(new ArrayList<>(), info);
        Pizza p = new Pizza("Test", TypePizza.VIANDE);

        c.ajouterPizza(p);

        assertEquals(1, c.getPizzas().size());
    }

    @Test
    public void testChangerStatut() {
        InformationPersonnelle info =
                new InformationPersonnelle("A", "B", "C", 20);

        Commande c = new Commande(new ArrayList<>(), info);

        c.setStatut(StatutCommande.VALIDEE);
        assertEquals(StatutCommande.VALIDEE, c.getStatut());
    }

    @Test
    public void testRetirerPizza() {
        InformationPersonnelle info =
                new InformationPersonnelle("A", "B", "C", 20);

        Commande c = new Commande(new ArrayList<>(), info);
        Pizza p = new Pizza("Test", TypePizza.VIANDE);

        c.ajouterPizza(p);
        c.retirerPizza(p);

        assertTrue(c.getPizzas().isEmpty());
    }
}
