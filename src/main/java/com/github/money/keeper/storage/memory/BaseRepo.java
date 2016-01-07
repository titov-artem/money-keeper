package com.github.money.keeper.storage.memory;

import java.util.List;

/**
 * Describe base repository actions
 *
 * @param <K> key class of stored objects
 * @param <V> stored objects class
 */
public interface BaseRepo<K, V> {

    List<V> save(Iterable<V> values);

    void delete(K key);

    List<V> loadAll();

    V load(K key);

    void clear();
}
