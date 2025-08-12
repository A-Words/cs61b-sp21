package randomizedtest;

import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
    @Test
    public void testThreeAddThreeRemove() {
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        noResizing.addLast(4);
        noResizing.addLast(5);
        noResizing.addLast(6);

        buggy.addLast(4);
        buggy.addLast(5);
        buggy.addLast(6);

        assertEquals(noResizing.removeLast(), buggy.removeLast());
        assertEquals(noResizing.removeLast(), buggy.removeLast());
        assertEquals(noResizing.removeLast(), buggy.removeLast());
    }

    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> noResizing = new AListNoResizing<>();
        BuggyAList<Integer> buggy = new BuggyAList<>();

        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                noResizing.addLast(randVal);
                buggy.addLast(randVal);
            } else if (operationNumber == 1) {
                // size
                int size = noResizing.size();
                int buggySize = buggy.size();
                assertEquals(size, buggySize);
            } else if (noResizing.size() > 0) {
                if (operationNumber == 2) {
                    // getLast
                    assertEquals(noResizing.getLast(), buggy.getLast());
                } else if (operationNumber == 3) {
                    // removeLast
                    assertEquals(noResizing.removeLast(), buggy.removeLast());
                }
            }
        }
    }
}
