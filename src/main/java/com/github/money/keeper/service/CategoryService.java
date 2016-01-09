package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Set;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
public class CategoryService {

    public static final String UNKNOWN_CATEGORY_NAME = "Unknown";

    private CategoryRepo categoryRepo;
    private StoreRepo storeRepo;

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

    @Required
    public void setCategoryRepo(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }
}
