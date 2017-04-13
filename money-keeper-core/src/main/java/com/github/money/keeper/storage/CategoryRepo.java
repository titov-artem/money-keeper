package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.Category;

import java.util.Map;
import java.util.Optional;

/**
 * Don't use directly, use CategoryService instead
 */
public interface CategoryRepo extends BaseRepo<Long, Category> {

    Optional<Category> findByName(String name);

    Map<String, Category> findByNames(Iterable<String> names);

}
