package com.github.money.keeper.storage;

import javax.annotation.Nullable;
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

    // todo maybe use optional here, not nullable
    @Nullable
    V load(K key);

    void clear();
}
