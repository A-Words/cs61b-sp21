package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;
    // If new BST, root != null. If not new BST, root.size == null.

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        } else {
            return root.contains(key);
        }
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        } else {
            return root.search(key).value;
        }
    }

    @Override
    public int size() {
        if (root == null) {
            return 0;
        } else {
            return root.size;
        }
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new BSTNode(key, value);
        } else {
            root.insert(key, value);
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    public void printInOrder() {
        printInOrderRec(root);
    }

    private void printInOrderRec(BSTNode node) {
        if (node != null) {
            printInOrderRec(node.left);
            System.out.print(node.key + " ");
            printInOrderRec(node.right);
        }
    }

    private class BSTNode {
        K key;
        V value;
        BSTNode left, right;
        int size;

        BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            left = right = null;
            size = 1;
        }

        void insert(K key, V value) {
            root = insertRec(root, key, value);
            size++;
        }

        BSTNode insertRec(BSTNode node, K key, V value) {
            if (node == null) {
                return new BSTNode(key, value);
            }
            if (key.compareTo(node.key) < 0) {
                node.left = insertRec(node.left, key, value);
            }
            if (key.compareTo(node.key) > 0) {
                node.right = insertRec(node.right, key, value);
            }
            return node;
        }

        BSTNode search(K key) {
            return searchRec(root, key);
        }

        BSTNode searchRec(BSTNode node, K key) {
            if (node == null || node.key.equals(key)) {
                return node;
            }
            if (key.compareTo(node.key) < 0) {
                return searchRec(node.left, key);
            } else {
                return searchRec(node.right, key);
            }
        }

        boolean contains(K key) {
            return searchRec(root, key) != null;
        }
    }
}
