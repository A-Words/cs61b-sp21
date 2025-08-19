package deque;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.Comparator;

public class MyMaxArrayDequeTest {
    private static class ComparatorA implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o1 - o2;
        }
    }

    private static class ComparatorB implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            if (o1.charAt(0) < o2.charAt(0)) {
                return -1;
            }
            if (o1.charAt(0) > o2.charAt(0)) {
                return 1;
            }
            return 0;
        }
    }
    private static class ComparatorC implements Comparator<Integer> {
        @Override
        public int compare(Integer o1, Integer o2) {
            return o2 - o1;
        }
    }

    @Test
    public void comparatorATest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new ComparatorA());
        for (int i = 0; i < 10; i++) {
            mad.addFirst(i);
        }
        assertEquals(9, (int) mad.max());
    }

    @Test
    public void comparatorBTest() {
        MaxArrayDeque<String> mad = new MaxArrayDeque<>(new ComparatorB());
        mad.addFirst("Dog");
        mad.addFirst("Cat");
        mad.addFirst("Bird");
        assertEquals("Dog", mad.max());
    }

    @Test
    public void comparatorCTest() {
        MaxArrayDeque<Integer> mad = new MaxArrayDeque<>(new ComparatorA());
        for (int i = 0; i < 10; i++) {
            mad.addFirst(i);
        }
        assertEquals(0, (int) mad.max(new ComparatorC()));
    }
}
