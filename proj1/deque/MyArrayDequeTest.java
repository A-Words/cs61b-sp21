package deque;

import org.junit.Test;
import static org.junit.Assert.*;

public class MyArrayDequeTest {
    @Test
    public void ArrayDequeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        assertEquals(0, a.size());
    }

    @Test
    public void isEmptyTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        assertTrue(a.isEmpty());
    }

    @Test
    public void addFirstTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addFirst(2);
        a.addFirst(3);
        assertEquals(3, a.size());
    }

    @Test
    public void isNotEmptyTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        assertFalse(a.isEmpty());
    }

    @Test
    public void addLastTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addFirst(2);
        a.addFirst(3);
        a.addLast(8);
        a.addLast(9);
        assertEquals(5, a.size());
    }

    @Test
    public void addLastBlankTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addLast(1);
        assertEquals(1, a.size());
    }

    @Test
    public void printDequeTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addLast(0);
        a.addFirst(2);
        a.addFirst(3);
        a.printDeque();
    }

    @Test
    public void resizeExpandTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        a.addFirst(1);
        a.addLast(0);
        a.addFirst(2);
        a.addFirst(3);
        a.addLast(-1);
        a.addLast(-2);
        a.addFirst(4);
        a.addFirst(5);
        a.addLast(-3);
        assertEquals(9, a.size());
    }

    @Test
    public void removeFirstTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 25; i++) {
            a.addFirst(i);
            a.addLast(i);
        }
        assertEquals(50, a.size());
        assertEquals(24, (int) (a.removeFirst()));
        assertEquals(49, a.size());
    }

    @Test
    public void removeLastTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 25; i++) {
            a.addFirst(i);
        }
        assertEquals(25, a.size());
        assertEquals(0, (int) (a.removeLast()));
        assertEquals(24, a.size());
    }

    @Test
    public void removeAndSharkTest() {
        ArrayDeque<Integer> a = new ArrayDeque<>();
        for (int i = 0; i < 25; i++) {
            a.addFirst(i);
            a.addLast(i);
        }
        assertEquals(50, a.size());
        for (int i = 0; i < 20; i++) {
            a.removeFirst();
            a.removeLast();
        }
        assertEquals(10, a.size());
    }
}
