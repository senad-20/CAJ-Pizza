package tests;

import org.junit.jupiter.api.Test;
import pizzas.*;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaTest {

    @Test
    public void testCreationOK() {
        Pizza p = new Pizza("Reine", TypePizza.VIANDE);
        assertEquals("Reine", p.getNom());
        assertEquals(TypePizza.VIANDE, p.getTypePizza());
        assertTrue(p.getIngredients().isEmpty());
        assertEquals(-1, p.getPrixfixe()); // default
    }

    @Test
    public void testAjouterIngredient() {
        Pizza p = new Pizza("Reine", TypePizza.VIANDE);
        Ingredient i = new Ingredient("jambon", 2);
        p.ajouterIngredient(i);

        assertTrue(p.getIngredients().contains(i));
    }

    @Test
    public void testEnleverIngredient() {
        Pizza p = new Pizza("Reine", TypePizza.VIANDE);
        Ingredient i = new Ingredient("jambon", 2);
        p.ajouterIngredient(i);
        p.enleverIngredient(i);

        assertFalse(p.getIngredients().contains(i));
    }

    @Test
    public void testAjouterNote() {
        Pizza p = new Pizza("Reine", TypePizza.VIANDE);
        InformationPersonnelle info = new InformationPersonnelle("Dupond", "Jean", "Paris", 25);
        Evaluation e = new Evaluation(info, 4, null);

        p.ajouternote(e);

        assertEquals(1, p.getEvaluations().size());

        Evaluation first = p.getEvaluations().stream().findFirst().orElseThrow();
        assertEquals(4, first.getNote());
    }


    @Test
    public void testEquals() {
        Pizza p1 = new Pizza("Reine", TypePizza.VIANDE);
        Pizza p2 = new Pizza("Reine", TypePizza.VIANDE);
        assertEquals(p1, p2);
    }

    @Test
    public void testHashCode() {
        Pizza p = new Pizza("Reine", TypePizza.VIANDE);
        assertEquals("Reine".hashCode(), p.hashCode());
    }
}

