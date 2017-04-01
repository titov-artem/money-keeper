package com.github.money.keeper.storage;

import com.google.common.collect.Multimap;

import java.util.List;
import java.util.Map;

/**
 * @author Artem Titov
 */
public interface ManyToOneAssociationRepo {
    void associate(String firstKey, String secondKey);

    void deleteByFirstKey(String key);

    void deleteBySecondKey(String key);

    String getByFirstKey(String key);

    List<String> getBySecondKey(String key);

    Map<String, String> getAllByFirstKey();

    Multimap<String, String> getAllBySecondKey();

    void clear();
}
