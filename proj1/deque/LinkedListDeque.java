package deque;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private class Node {
        public T item;
        public Node next;
        public Node previous; // previous 需手动修改

        public Node(T t, Node n) {
            this.item = t;
            this.next = n;
        }
    }

    private final Node sentinel;
    private int size;

    /** Adds an item of type {@code T} to the front of the deque. */
    @Override
    public void addFirst(T item) {
        sentinel.next = new Node(item, sentinel.next);
        sentinel.next.next.previous = sentinel.next;
        sentinel.next.previous = sentinel; //修复 previous 为空
        size++;
    }

    /** Adds an item of type {@code T} to the back of the deque. */
    @Override
    public void addLast(T item) {
        sentinel.previous.next = new Node(item, sentinel);
        sentinel.previous.next.previous = sentinel.previous;
        sentinel.previous = sentinel.previous.next;
        size++;
    }

    /** Returns the number of items in the deque. */
    @Override
    public int size() {
        return size;
    }

    /**
     * Prints the items in the deque from first to last, separated by a space.
     * Once all the items have been printed, print out a new line.
     */
    @Override
    public void printDeque() {
//        System.out.print("Deque: ");
        for (Node n = sentinel.next; n != sentinel; n = n.next) {
            System.out.print(n.item + " ");
        }
        System.out.println();

//        for (Node n = sentinel.next; n != sentinel; n = n.next) {
//            System.out.print(n.item);
//            if (n.next != sentinel) {
//                System.out.print(" ");
//            } else {
//                System.out.println();
//            }
//        }
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        }
        T removeItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.previous = sentinel;
        size--;
        return removeItem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T removeLast() {
        if (sentinel.next == sentinel) {
            return null;
        }
        T removeItem = sentinel.previous.item;
        sentinel.previous = sentinel.previous.previous;
        sentinel.previous.next = sentinel;
        size--;
        return removeItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T get(int index) {
//        if (index < 0 || index >= size) {
//            return null;
//        }
//        Node n = sentinel.next;
//        for (int i = 0; i < index; i++) {
//            n = n.next;
//        }
//        return n.item;
        int i = 0;
        for (T t : this) {
            if (i == index) {
                return t;
            }
            i++;
        }
        return null;
    }

    /**
     * The Deque objects we’ll make are iterable (i.e., {@code Iterable<T>})
     * so we must provide this method to return an iterator.
     */
    @Override
    public Iterator<T> iterator() {
        return new LinkedListDequeIterator();
    }

    private class LinkedListDequeIterator implements Iterator<T> {
        Node currentNode = sentinel.next;

        @Override
        public boolean hasNext() {
            return currentNode != sentinel;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T currentItem = currentNode.item;
            currentNode = currentNode.next;
            return currentItem;
        }
    }

    /**
     * Returns whether the parameter {@code o} is equal to the Deque.
     * {@code o} is considered equal if it is a Deque and
     * if it contains the same contents in the same order.
     */
    @Override
    public boolean equals(Object o) {
        if (o instanceof LinkedListDeque) {
            LinkedListDeque<T> lld = (LinkedListDeque<T>) o;
            if (size != lld.size()) {
                return false;
            }
            for (int i = 0; i < lld.size; i++) {
                if (!lld.get(i).equals(this.get(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /** Creates an empty linked list deque. */
    public LinkedListDeque() {
        sentinel = new Node(null, null);
        sentinel.next = sentinel;
        sentinel.previous = sentinel;
        size = 0;
    }

    /** Same as {@link #get(int)}, but uses recursion. */
    public T getRecursive(int index) {
        if (index < 0 || index >= size) {
            return null;
        }
        return getRecursive(index, sentinel.next);
    }

    /** Assist in completing recursion on index and node. */
    private T getRecursive(int index, Node n) {
        if (index == 0) {
            return n.item;
        }
        return getRecursive(index - 1, n.next);
    }
}
