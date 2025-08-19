package deque;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

public class MyLinkedListDequeTest {
    @Test
    public void linkedListDequeTest() {
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

    @Test
    public void randomizedTest() {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 2);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                lld.addLast(randVal);
            } else if (operationNumber == 1) {
                // addFirst
                int randVal = StdRandom.uniform(0, 100);
                lld.addFirst(randVal);
            }
        }
        assertEquals(N, lld.size());
    }
}
