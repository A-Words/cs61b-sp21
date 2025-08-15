package deque;

import java.util.Iterator;

public interface Deque<T> {
    /** Adds an item of type {@code T} to the front of the deque. */
    void addFirst(T item);
    /** Adds an item of type {@code T} to the back of the deque. */
    void addLast(T item);
    /** Returns {@code true} if deque is empty, {@code false} otherwise. */
    boolean isEmpty();
    /** Returns the number of items in the deque. */
    int size();
    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    void printDeque();
    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns {@code null}.
     */
    T removeFirst();
    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns {@code null}.
     */
    T removeLast();
    /**
     * Gets the item at the given index, where 0 is the front.
     * If no such item exists, returns {@code null}.
     */
    T get(int index);

    /**
     * The Deque objects we’ll make are iterable (i.e., {@code Iterable<T>})
     * so we must provide this method to return an iterator.
     */
    //Iterator<T> iterator();
    /**
     * Returns whether the parameter {@code o} is equal to the Deque.
     * {@code o} is considered equal if it is a Deque and
     * if it contains the same contents in the same order.
     */
    //boolean equals(Object o);
}
