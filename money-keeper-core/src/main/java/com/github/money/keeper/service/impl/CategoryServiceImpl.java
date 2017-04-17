package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.jdbc.TxHelper;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.*;

import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;
    private StoreRepo storeRepo;
    private TxHelper txHelper;

    private Category defaultCategory;

    @Inject
    public CategoryServiceImpl(CategoryRepo categoryRepo,
                               StoreRepo storeRepo,
                               TxHelper txHelper) {
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
        this.txHelper = txHelper;
    }

    @PostConstruct
    public void init() {
        Optional<Category> defaultCategoryOpt = categoryRepo.findByName(DEFAULT_CATEGORY_NAME);
        if (defaultCategoryOpt.isPresent()) {
            defaultCategory = defaultCategoryOpt.get();
            return;
        }

        defaultCategory = categoryRepo.save(new Category(DEFAULT_CATEGORY_NAME, emptySet()));
    }

    @Override public Category getDefaultCategory() {
        return defaultCategory;
    }

    @Override
    public Map<String, Category> findByNames(Iterable<String> names) {
        return categoryRepo.findByNames(names);
    }

    @Override
    public List<Category> save(Iterable<Category> categories) {
        return categoryRepo.save(categories);
    }

    @Override
    public Category rename(Long categoryId, String name) {
        return txHelper.withTx(() -> {
            Optional<Category> categoryOpt = categoryRepo.get(categoryId);
            Category category = categoryOpt.orElseThrow(NoSuchElementException::new);

            if (categoryRepo.findByName(name).isPresent()) {
                throw new IllegalArgumentException("Category with name " + name + " already exists");
            }
            if (category.getName().equals(name)) {
                return category;
            }
            category = category.rename(name);
            return categoryRepo.save(category);
        });
    }

    @Override
    public Category union(String name, Collection<Long> categoryIds) {
        return txHelper.withTx(() -> {
            List<Category> categories = categoryRepo.get(categoryIds);
            if (categories.size() != categoryIds.size()) {
                throw new NoSuchElementException("Some categories not found: " + Sets.difference(
                        new HashSet<>(categoryIds), categories.stream().map(Category::getId).collect(toSet())
                ));
            }

            boolean nameNotBelongToUnion = categories.stream()
                    .map(Category::getName)
                    .noneMatch(name::equals);
            if (nameNotBelongToUnion && categoryRepo.findByName(name).isPresent()) {
                throw new IllegalArgumentException("Category with name " + name + " already exists");
            }

            Set<String> alternatives = Sets.newHashSet();
            Set<Long> storeIds = Sets.newHashSet();
            for (Category c : categories) {
                alternatives.addAll(c.getAlternatives());
                storeIds.addAll(
                        storeRepo.findAllByCategory(c.getId()).stream().map(Store::getId).collect(toSet())
                );
            }
            Category category = categoryRepo.save(new Category(name, alternatives));
            storeRepo.setCategoryId(storeIds, category.getId());
            categoryRepo.delete(categoryIds);
            return category;
        });
    }

    @Override
    public void delete(Long id) {
        if (defaultCategory.getId().equals(id)) {
            throw new IllegalArgumentException("Can't delete default category");
        }
        Optional<Category> categoryOpt = categoryRepo.get(id);
        Category category = categoryOpt.orElseThrow(NoSuchElementException::new);

        List<Store> stores = storeRepo.findAllByCategory(category.getId());
        if (!stores.isEmpty()) {
            throw new IllegalStateException("Category " + category.getName() + " not empty!");
        }

        categoryRepo.delete(id);
    }

}
