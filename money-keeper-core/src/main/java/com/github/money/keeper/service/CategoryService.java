package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;

import java.util.Collection;
import java.util.List;

public interface CategoryService {
    String UNKNOWN_CATEGORY_NAME = "Unknown";

    void updateCategories();

    CategorizationHelper getCategorizationHelper();

    Category load(String name);

    List<Category> load(Collection<String> categoryNames);

    List<Category> loadAll();

    Category rename(String oldName, String newName);

    Category union(String name, Iterable<Category> categories);

    void changeCategory(Store store, Category category);

    void delete(String name);
}