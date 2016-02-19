package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.StoreToCategoryRepo;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;
    private StoreRepo storeRepo;
    private StoreToCategoryRepo storeToCategoryRepo;

    @Override
    public void updateCategories() {
        List<Category> categories = categoryRepo.loadAll();

        Set<String> alternatives = categories.stream()
                .flatMap(c -> c.getAlternatives().stream())
                .collect(toSet());

        List<Store> stores = storeRepo.loadAll();

        Set<Category> toCreate = stores.stream()
                .map(Store::getCategoryDescription)
                .map(c -> StringUtils.isBlank(c) ? UNKNOWN_CATEGORY_NAME : c)
                .filter(c -> !alternatives.contains(c))
                .map(c -> new Category(c, singleton(c)))
                .collect(toSet());
        categoryRepo.save(toCreate);
    }

    @Override
    public CategorizationHelper getCategorizationHelper() {
        Map<String, Category> categoryNameToCategory = categoryRepo.loadAll().stream().collect(toMap(Category::getName, c -> c));
        Map<String, Category> storeNameToCategory = storeToCategoryRepo.getAllByFirstKey().entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> categoryNameToCategory.get(e.getValue())));
        return new AutoAndManualCategorizationHelper(categoryRepo.loadAll(), storeNameToCategory);
    }

    @Override
    public Category load(String name) {
        return categoryRepo.load(name);
    }

    @Override
    public List<Category> loadAll() {
        return categoryRepo.loadAll();
    }

    @Override
    public Category rename(String oldName, String newName) {
        Category oldCategory = categoryRepo.load(oldName);
        if (oldCategory == null) {
            throw new IllegalArgumentException("No such category " + oldName);
        }
        if (oldName.equals(newName)) {
            return oldCategory;
        }
        Category newCategory = new Category(newName, oldCategory.getAlternatives());

        List<String> storeNames = storeToCategoryRepo.getBySecondKey(oldCategory.getName());

        categoryRepo.save(singleton(newCategory));
        for (String storeName : storeNames) {
            storeToCategoryRepo.associate(storeName, newCategory.getName());
        }

        categoryRepo.delete(oldName);
        storeToCategoryRepo.deleteBySecondKey(oldCategory.getName());
        return newCategory;
    }

    @Override
    public Category union(String name, Iterable<Category> categories) {
        Set<String> alternatives = Sets.newHashSet();
        Set<String> storeNames = Sets.newHashSet();
        for (Category c : categories) {
            categoryRepo.delete(c.getName());
            alternatives.addAll(c.getAlternatives());
            storeNames.addAll(storeToCategoryRepo.getBySecondKey(c.getName()));
            storeToCategoryRepo.deleteBySecondKey(c.getName());
        }
        Category category = new Category(name, alternatives);
        categoryRepo.save(singleton(category));
        for (String storeName : storeNames) {
            storeToCategoryRepo.associate(storeName, category.getName());
        }
        return category;
    }

    @Required
    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }

    @Required
    public void setStoreToCategoryRepo(StoreToCategoryRepo storeToCategoryRepo) {
        this.storeToCategoryRepo = storeToCategoryRepo;
    }
}
