package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class MyLinkedListDequeTest {
    @Test
    public void LinkedListDequeTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        assertEquals(0, l.size());
    }

    @Test
    public void isEmptyTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        assertTrue(l.isEmpty());
    }

    @Test
    public void addFirstTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        assertEquals(3, l.size());
    }

    @Test
    public void isNotEmptyTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        assertFalse(l.isEmpty());
    }

    @Test
    public void addLastTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        l.addLast(8);
        l.addLast(9);
        assertEquals(5, l.size());
    }

    @Test
    public void addLastBlankTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addLast(1);
        assertEquals(1, l.size());
    }

    @Test
    public void printDequeTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        l.printDeque();
    }

    @Test
    public void removeFirstTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        assertEquals(3, (int) l.removeFirst());
        assertEquals(2, l.size());
    }

    @Test
    public void removeLastTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        assertEquals(1, (int) l.removeLast());
        assertEquals(2, l.size());
    }

    @Test
    public void getTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        assertEquals(2, (int) l.get(1));
        assertEquals(1, (int) l.get(2));
    }

    @Test
    public void getRecursiveTest() {
        LinkedListDeque<Integer> l = new LinkedListDeque<>();
        l.addFirst(1);
        l.addFirst(2);
        l.addFirst(3);
        assertEquals(2, (int) l.getRecursive(1));
        assertEquals(1, (int) l.getRecursive(2));
    }
}
