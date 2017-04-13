package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.storage.CategoryRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.jdbc.TxHelper;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static java.util.stream.Collectors.toSet;

/**
 * @author Artem Titov
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private CategoryRepo categoryRepo;
    private StoreRepo storeRepo;
    private TxHelper txHelper;

    @Inject
    public CategoryServiceImpl(CategoryRepo categoryRepo,
                               StoreRepo storeRepo,
                               TxHelper txHelper) {
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
        this.txHelper = txHelper;
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
            category.rename(name);
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
        Optional<Category> categoryOpt = categoryRepo.get(id);
        Category category = categoryOpt.orElseThrow(NoSuchElementException::new);

        List<Store> stores = storeRepo.findAllByCategory(category.getId());
        if (!stores.isEmpty()) {
            throw new IllegalStateException("Category " + category.getName() + " not empty!");
        }

        categoryRepo.delete(id);
    }


    /*
    Old methods
     */

    @Override
    public void updateCategories() {
//        List<Category> categories = categoryRepo.getAll();
//
//        Set<String> alternatives = categories.stream()
//                .flatMap(c -> c.getAlternatives().stream())
//                .collect(toSet());
//
//        List<Store> stores = storeRepo.getAll();
//
//        Set<Category> toCreate = stores.stream()
//                .map(Store::getCategoryId)
//                .map(c -> StringUtils.isBlank(c) ? UNKNOWN_CATEGORY_NAME : c)
//                .filter(c -> !alternatives.contains(c))
//                .map(c -> new Category(c, singleton(c)))
//                .collect(toSet());
//        categoryRepo.save(toCreate);
    }

    @Override
    public CategorizationHelper getCategorizationHelper() {
//        Map<String, Category> categoryNameToCategory = categoryRepo.getAll().stream().collect(toMap(Category::getName, c -> c));
//        Map<String, Category> storeNameToCategory = storeToCategoryRepo.getAllByFirstKey().entrySet().stream()
//                .collect(toMap(Map.Entry::getKey, e -> categoryNameToCategory.get(e.getValue())));
//        return new AutoAndManualCategorizationHelper(categoryRepo.getAll(), storeNameToCategory);
        return null;
    }

    @Override
    public Category load(String name) {
//        return categoryRepo.get(name);
        return null;
    }

    @Override
    public List<Category> load(Collection<String> categoryNames) {
//        return categoryRepo.get(categoryNames);
        return null;
    }

    @Override
    public List<Category> loadAll() {
        return categoryRepo.getAll();
    }

    @Override
    public void changeCategory(Store store, Category category) {
//        storeToCategoryRepo.deleteByFirstKey(store.getName());
//        storeToCategoryRepo.associate(store.getName(), category.getName());
    }

}
