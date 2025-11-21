package tests;

import org.junit.jupiter.api.Test;
import pizzas.Ingredient;

import static org.junit.jupiter.api.Assertions.*;

public class IngredientTest {

    @Test
    public void testCreationOK() {
        Ingredient i = new Ingredient("tomate", 1.5);
        assertEquals("tomate", i.getNom());
        assertEquals(1.5, i.getPrix());
    }

    @Test
    public void testNomInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("", 1);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient(null, 1);
        });
    }

    @Test
    public void testPrixInvalide() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Ingredient("tomate", -1);
        });
    }

    @Test
    public void testSetPrixOK() {
        Ingredient i = new Ingredient("tomate", 1);
        i.setPrix(2);
        assertEquals(2, i.getPrix());
    }

    @Test
    public void testSetPrixNegatif() {
        Ingredient i = new Ingredient("tomate", 1);
        assertThrows(IllegalArgumentException.class, () -> i.setPrix(-3));
    }

    @Test
    public void testEquals() {
        Ingredient i1 = new Ingredient("tomate", 1);
        Ingredient i2 = new Ingredient("tomate", 5);
        assertEquals(i1, i2);
    }

    @Test
    public void testHashCode() {
        Ingredient i = new Ingredient("tomate", 1);
        assertEquals("tomate".hashCode(), i.hashCode());
    }
}
