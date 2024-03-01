package kafka.common.utils;

/**
 * @author phongpq
 */

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * A simple read-optimized map implementation that synchronizes only writes and does a full copy on each modification
 */
public class CopyOnWriteMap<K, V> implements ConcurrentMap<K, V> {
    private volatile Map<K, V> map;

    public CopyOnWriteMap() {
        this.map = Collections.emptyMap();
    }

    public CopyOnWriteMap(Map<K, V> map) {
        this.map = Collections.unmodifiableMap(map);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return map.get(key);
    }

    @Override
    public synchronized V put(K key, V value) {
        Map<K, V> copy = new java.util.HashMap<>(this.map);
        V prev = copy.put(key, value);
        this.map = Collections.unmodifiableMap(copy);
        return prev;
    }

    @Override
    public synchronized V remove(Object key) {
        Map<K, V> copy = new java.util.HashMap<>(this.map);
        V prev = copy.remove(key);
        this.map = Collections.unmodifiableMap(copy);
        return prev;
    }

    @Override
    public synchronized void putAll(Map<? extends K, ? extends V> m) {
        Map<K, V> copy = new java.util.HashMap<>(this.map);
        copy.putAll(m);
        this.map = Collections.unmodifiableMap(copy);
    }

    @Override
    public void clear() {
        this.map = Collections.emptyMap();
    }

    @Override
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<V> values() {
        return map.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

    @Override
    public synchronized V putIfAbsent(K key, V value) {
        if (!containsKey(key)) {
            return put(key, value);
        } else {
            return get(key);
        }
    }

    @Override
    public synchronized boolean remove(Object key, Object value) {
        if (containsKey(key) && get(key).equals(value)) {
            remove(key);
            return true;
        }
        return false;
    }

    @Override
    public synchronized boolean replace(K key, V original, V replacement) {
        if (containsKey(key) && get(key).equals(original)) {
            put(key, replacement);
            return true;
        }
        return false;
    }

    @Override
    public synchronized V replace(K key, V value) {
        if (containsKey(key)) {
            return put(key, value);
        }
        return null;
    }
}
