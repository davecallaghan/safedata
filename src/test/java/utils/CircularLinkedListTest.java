package utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CircularLinkedListTest {

    private static CircularLinkedList cll = new CircularLinkedList();

    @BeforeAll
    public static void init() {
        cll.add(13);
        cll.add(7);
        cll.add(24);
        cll.add(1);
        cll.add(8);
        cll.add(37);
        cll.add(46);
    }

    @Test
    public void givenACircularLinkedList_whenLookingForExistingElements_ThenListReturnsTrue() {
        assertTrue(cll.contains(8));
        assertTrue(cll.contains(37));
    }

    @Test
    public void givenACircularLinkedList_whenLookingForNonExistingElements_ThenListReturnsFalse() {
        assertFalse(cll.contains(100));
    }

    @Test
    public void givenACircularLinkedList_whenDeletingElement_ThenListDoesNotContainElement() {
        assertTrue(cll.contains(46));
        cll.delete(46);
        assertFalse(cll.contains(46));

        assertTrue(cll.contains(1));
        cll.delete(1);
        assertFalse(cll.contains(1));

        assertTrue(cll.contains(13));
        cll.delete(13);
        assertFalse(cll.contains(13));
    }

}
