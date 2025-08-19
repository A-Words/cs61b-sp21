package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;

    /**
     * 使用给定的 {@code Comparator} 创建一个 {@code MaxArrayDeque}
     * @param c Comparator
     */
    public MaxArrayDeque(Comparator<T> c) {
        defaultComparator = c;
    }

    /**
     * 返回双端队列中由构造函数 {@code Comparator} 确定的最大元素。
     * 如果 {@code MaxArrayDeque} 为空，则直接返回 {@code null}。
     */
    public T max() {
        return max(defaultComparator);
    }


    /**
     * 返回双端队列中由参数 {@code c} 所确定的最大元素。
     * 如果 {@code MaxArrayDeque} 为空，则直接返回 {@code null}。
     * @param c Comparator
     */
    public T max(Comparator<T> c) {
        if (isEmpty()) {
            return null;
        }
        T maxItem = this.get(0);
        for (T i : this) {
            if (c.compare(i, maxItem) > 0) {
                maxItem = i;
            }
        }
        return maxItem;
    }
}
