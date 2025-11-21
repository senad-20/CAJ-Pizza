package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pizzas.*;

import static org.junit.jupiter.api.Assertions.*;

public class PizzaioloTest {

    private Pizzaiolo p;
    private InformationPersonnelle info;

    @BeforeEach
    public void setup() {
        p = new Pizzaiolo();
        info = new InformationPersonnelle("Dupont", "Jean", "1 rue de Paris", 43);
    }

    @Test
    public void testInscriptionOK() {
        int res = p.inscription("test@mail.com", "abc", info);
        assertEquals(0, res);
    }

    @Test
    public void testInscriptionEmailDejaPris() {
        p.inscription("test@mail.com", "abc", info);
        int res = p.inscription("test@mail.com", "xyz", info);
        assertEquals(-1, res);
    }

    @Test
    public void testConnexionOK() {
        p.inscription("a@b.com", "pass", info);
        assertTrue(p.connexion("a@b.com", "pass"));
    }

    @Test
    public void testCreerPizzaOK() {
        Pizza pizza = p.creerPizza("Reine", TypePizza.VIANDE);
        assertNotNull(pizza);
    }

    @Test
    public void testInterdireIngredient() {
        p.creerIngredient("jambon", 2);
        boolean ok = p.interdireIngredient("jambon", TypePizza.VEGETARIENNE);
        assertTrue(ok);
    }

    @Test
    public void testAjouterIngredientInterdit() {
        p.creerIngredient("jambon", 2);
        p.interdireIngredient("jambon", TypePizza.REGIONALE);

        Pizza veg = p.creerPizza("Veggie", TypePizza.REGIONALE);

        int res = p.ajouterIngredientPizza(veg, "jambon");
        assertEquals(-3, res);
    }

    @Test
    public void testPrixMinimal() {
        p.creerIngredient("fromage", 2);
        p.creerIngredient("tomate", 1);

        Pizza pizza = p.creerPizza("Marguerite", TypePizza.VIANDE);
        p.ajouterIngredientPizza(pizza, "fromage");
        p.ajouterIngredientPizza(pizza, "tomate");

        double prix = p.calculerPrixMinimalPizza(pizza);

        assertEquals(4.2, prix, 0.01);  // 3 × 1.4 = 4.2
    }
}
