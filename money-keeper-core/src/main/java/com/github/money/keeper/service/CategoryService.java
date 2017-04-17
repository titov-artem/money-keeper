package com.github.money.keeper.service;

import com.github.money.keeper.model.core.Category;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CategoryService {
    String DEFAULT_CATEGORY_NAME = "Unknown";

    Category getDefaultCategory();

    Map<String, Category> findByNames(Iterable<String> names);

    List<Category> save(Iterable<Category> categories);

    Category rename(Long categoryId, String name);

    Category union(String name, Collection<Long> categoryIds);

    void delete(Long id);

}
