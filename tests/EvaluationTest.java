package tests;

import org.junit.jupiter.api.Test;
import pizzas.*;

import static org.junit.jupiter.api.Assertions.*;

public class EvaluationTest {

    @Test
    public void testCreationSansCommentaire() {
        InformationPersonnelle info = new InformationPersonnelle("A", "B", "C", 20);
        Evaluation e = new Evaluation(info, 4,null);

        assertEquals(4, e.getNote());
        assertNull(e.getCommentaire());
    }

    @Test
    public void testCreationAvecCommentaire() {
        InformationPersonnelle info = new InformationPersonnelle("A", "B", "C", 20);
        Evaluation e = new Evaluation(info, 4, "Très bon");

        assertEquals("Très bon", e.getCommentaire());
    }

    @Test
    public void testNoteInvalide() {
        InformationPersonnelle info = new InformationPersonnelle("A", "B", "C", 20);
        assertThrows(IllegalArgumentException.class, () -> new Evaluation(info, -1,null));
        assertThrows(IllegalArgumentException.class, () -> new Evaluation(info, 6,null));
    }

    @Test
    public void testCommentaireInvalide() {
        InformationPersonnelle info = new InformationPersonnelle("A", "B", "C", 20);
        assertThrows(IllegalArgumentException.class, () -> new Evaluation(info, 3, ""));
    }
}

