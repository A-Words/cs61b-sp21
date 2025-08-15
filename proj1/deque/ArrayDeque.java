package deque;

public class ArrayDeque<T> implements Deque<T> {
    /** 数组起始大小 */
    private static final int START_SIZE = 8;
    /** 最低负载率，达到后收缩 Array */
    private static final double LOWEST_LOAD = 0.25;
    /** 扩展率，Array 满载后据此倍数扩大 */
    private static final double EXPAND_RATE = 1.5;
    /** 收缩率，Array 达到最低负载率后据此倍数缩小 */
    private static final double SHARK_RATE = 0.5;

    private T[] dequeArray;
    private int head; // 向右一位为队列首元素
    private int tail; // 向左一位为队列尾元素
    private int size;

    enum DequeLocation { HEAD, TAIL }
    enum Direction { LEFT, RIGHT }

    /** Creates an empty array deque. */
    public ArrayDeque() {
        dequeArray = (T[]) new Object[START_SIZE];
        head = 0;
        tail = 1;
        size = 0;
    }

    /**
     * 将 Array 视为一个环，对 head 和 tail 进行移动
     * @param location 移动端点，HEAD 或 TAIL
     * @param direction 移动方向，LEAF 或 RIGHT
     */
    private void moveCycle(DequeLocation location, Direction direction) {
//        switch (location) {
//            case HEAD:
//                switch (direction) {
//                    case LEFT:
//                        head = (head - 1 + dequeArray.length) % dequeArray.length;
//                        break;
//                    case RIGHT:
//                        head = (head + 1 + dequeArray.length) % dequeArray.length;
//                        break;
//                }
//                break;
//            case TAIL:
//                switch (direction) {
//                    case LEFT:
//                        tail = (tail - 1 + dequeArray.length) % dequeArray.length;
//                        break;
//                    case RIGHT:
//                        tail = (tail + 1 + dequeArray.length) % dequeArray.length;
//                }
//                break;
//        }

        if (location == DequeLocation.HEAD) {
            if (direction == Direction.LEFT) {
                head = (head - 1 + dequeArray.length) % dequeArray.length;
            } else if (direction == Direction.RIGHT) {
                head = (head + 1 + dequeArray.length) % dequeArray.length;
            }
        } else if (location == DequeLocation.TAIL) {
            if (direction == Direction.LEFT) {
                tail = (tail - 1 + dequeArray.length) % dequeArray.length;
            } else if (direction == Direction.RIGHT) {
                tail = (tail + 1 + dequeArray.length) % dequeArray.length;
            }
        }
    }

    /**
     * 调整 Array 大小
     * @param newSize Array 调整后的大小
     */
    private void resize(int newSize) {
        if (newSize < size) {
            return;
        }

        T[] newSizeDequeArray = (T[]) new Object[newSize];
        for (int i = 0; i < size; i++) {
            int h = head + 1;
            if (h == dequeArray.length) {
                h = 0;
            }
            newSizeDequeArray[i] = dequeArray[h];
            moveCycle(DequeLocation.HEAD, Direction.RIGHT);
        }

        dequeArray = newSizeDequeArray;
        head = dequeArray.length - 1;
        tail = size;
    }

    /**
     * 当 Array 满时扩展，不足最低负载率且大于起始大小时收缩
     */
    private void smartResize() {
        if (size == dequeArray.length) {
            //expandArray();
            resize((int) (dequeArray.length * EXPAND_RATE));
        }
        if (size > START_SIZE && size <= dequeArray.length * LOWEST_LOAD) {
            //sharkArray();
            resize((int) (dequeArray.length * SHARK_RATE));
        }
    }

    /** Adds an item of type {@code T} to the front of the deque. */
    @Override
    public void addFirst(T item) {
        smartResize();
        dequeArray[head] = item;
        moveCycle(DequeLocation.HEAD, Direction.LEFT);
        size++;
    }

    /** Adds an item of type {@code T} to the back of the deque. */
    @Override
    public void addLast(T item) {
        smartResize();
        dequeArray[tail] = item;
        moveCycle(DequeLocation.TAIL, Direction.RIGHT);
        size++;
    }

    /** Returns {@code true} if deque is empty, {@code false} otherwise. */
    @Override
    public boolean isEmpty() {
        return size == 0;
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
//        int h = head + 1;
//        while (h != tail) {
//            if (h == dequeArray.length) {
//                h = 0;
//            }
//            System.out.print(dequeArray[h] + " ");
//            h++;
//        }

        for (int i = 0; i < size; i++) {
            int h = head + 1;
            if (h == dequeArray.length) {
                h = 0;
            }
            System.out.print(dequeArray[h] + " ");
            moveCycle(DequeLocation.HEAD, Direction.RIGHT);
        }

        System.out.println();
    }

    /**
     * Removes and returns the item at the front of the deque.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        moveCycle(DequeLocation.HEAD, Direction.RIGHT);
        T removeItem = dequeArray[head];
        dequeArray[head] = null;
        size--;
        smartResize();
        return removeItem;
    }

    /**
     * Removes and returns the item at the back of the deque.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        moveCycle(DequeLocation.TAIL, Direction.LEFT);
        T removeItem = dequeArray[tail];
        dequeArray[tail] = null;
        size--;
        smartResize();
        return removeItem;
    }

    /**
     * Gets the item at the given index, where 0 is the front.
     * If no such item exists, returns {@code null}.
     */
    @Override
    public T get(int index) {
        if (index < size) {
            int arrayIndex = (head + 1 + index + dequeArray.length) % dequeArray.length;
            return dequeArray[arrayIndex];
        }
        return null;
    }
}
