package com.github.money.keeper.service;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    String UNKNOWN_CATEGORY_NAME = "Unknown";

    Map<String, Category> findByNames(Iterable<String> names);

    List<Category> save(Iterable<Category> categories);

    Category rename(Long categoryId, String name);

    Category union(String name, Collection<Long> categoryIds);

    void delete(Long id);

    /*
    Old methods. Remove useless
     */

    void updateCategories();

    CategorizationHelper getCategorizationHelper();

    Category load(String name);

    List<Category> load(Collection<String> categoryNames);

    List<Category> loadAll();

    void changeCategory(Store store, Category category);


}
