package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.AbstractEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Describe base repository actions
 *
 * @param <K> key class of stored objects
 * @param <V> stored objects class
 */
public interface BaseRepo<K, V extends AbstractEntity<K>> {

    V save(V value);

    List<V> save(Iterable<V> values);

    void delete(K key);

    void delete(Iterable<K> keys);

    Optional<V> get(K key);

    List<V> get(Collection<K> keys);

    List<V> getAll();

    void clear();
}
