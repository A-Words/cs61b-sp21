package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private int num_elements;
    private double load_factor;
    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        load_factor = maxLoad;
        num_elements = 0;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }

    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = createBucket();
        }
        num_elements = 0;
    }

    @Override
    public boolean containsKey(K key) {
        int index = keyHashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get(K key) {
        int index = keyHashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                return node.value;
            }
        }
        return null;
    }

    @Override
    public int size() {
        return num_elements;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            updateValue(key, value);
        } else {
            if ((double) num_elements / buckets.length >= load_factor) {
                resizeTable();
            }
            Node node = createNode(key, value);
            nodeToTable(node, buckets);
            num_elements++;
        }
    }

    private void updateValue(K key, V value) {
        int index = keyHashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                node.value = value;
                break;
            }
        }
    }

    private void resizeTable() {
        int resize_num_buckets = buckets.length * 2;
        Collection<Node>[] resize_hash_table = createTable(resize_num_buckets);
        for (int i = 0; i < resize_num_buckets; i++) {
            resize_hash_table[i] = createBucket();
        }
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                nodeToTable(node, resize_hash_table);
            }
        }
        buckets = resize_hash_table;
    }

    private void nodeToTable(Node node, Collection<Node>[] table) {
        int index = keyHashIndex(node.key);
        table[index].add(node);
    }

    private int keyHashIndex(K key) {
        int hash = key.hashCode();
        if (hash < 0) {
            hash = -hash;
        }
        return hash  % buckets.length;
    }

    @Override
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Collection<Node> bucket : buckets) {
            for (Node node : bucket) {
                keys.add(node.key);
            }
        }
        return keys;
    }

    @Override
    public V remove(K key) {
        V remove_value = null;
        int index = keyHashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                remove_value = node.value;
                buckets[index].remove(node);
                num_elements--;
                break;
            }
        }
        return remove_value;
    }

    @Override
    public V remove(K key, V value) {
        V remove_value = null;
        int index = keyHashIndex(key);
        for (Node node : buckets[index]) {
            if (node.key.equals(key)) {
                if (node.value.equals(value)) {
                    remove_value = node.value;
                    buckets[index].remove(node);
                    num_elements--;
                }
                break;
            }
        }
        return remove_value;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
