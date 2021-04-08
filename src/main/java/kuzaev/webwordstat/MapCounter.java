package kuzaev.webwordstat;

import java.util.HashMap;

/**
 * A map data structure with long data type of value which is a counter<br>
 * When putting info in this structure value will be automatically incremented
 *
 * @param <K> data type of a key
 */
public class MapCounter<K extends Comparable<? super K>> extends HashMap<K, Long> {

    /**
     * Associates the specified value with the counter value in this map.
     * If the map previously contained a mapping for the key, the old
     * value is replaced with the incremented value.
     *
     * @param key for which the counter must be initialized or incremented
     * @return the previous counter value for {@code key}, or
     *         {@code null} if {@code key} was not counted yet.
     * @throws IllegalStateException if the specified key cannot be counted
     *         further because of overflow
     * @throws ClassCastException if the specified key cannot be compared
     *         with the keys currently in the map
     */
    public Long put(K key) {
        Long oldValue = get(key);
        oldValue = oldValue != null? oldValue : 0L;
        if (oldValue == Long.MAX_VALUE)
            throw new IllegalStateException("Counter overflow!");
        return super.put(key, oldValue + 1L);
    }

    /**
     * Same as HashMap.put(K key, V value) except that the negative values
     * for value are not allowed.
     */
    @Override
    public Long put(K key, Long value) {
        if (value < 0L)
            throw new IllegalArgumentException("The counter value of " + value + " is less than zero.");
        return super.put(key, value);
    }

}
