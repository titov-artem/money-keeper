package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.service.CategoryService;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class AutoAndManualCategorizationHelper implements CategorizationHelper {

    private final Map<String, Category> alternativeToCategory = Maps.newHashMap();
    private final Map<String, Category> manualMapping;

    public AutoAndManualCategorizationHelper(List<Category> categories, Map<String, Category> manualMapping) {
        this.manualMapping = manualMapping;
        categories.stream().forEach(
                c -> c.getAlternatives().forEach(
                        a -> Preconditions.checkState(alternativeToCategory.put(a, c) == null, "Duplicated alternative in different categories")
                )
        );
    }

    @Override
    @Nonnull
    public Category determineCategory(Store store) {
        Category category = manualMapping.get(store.getName());
        if (category != null) {
            return category;
        }
        String categoryDescription = store.getCategoryDescription();
        if (StringUtils.isBlank(categoryDescription)) {
            categoryDescription = CategoryService.UNKNOWN_CATEGORY_NAME;
        }
        category = alternativeToCategory.get(categoryDescription);
        if (category == null) {
            category = new Category(categoryDescription, ImmutableSet.of(categoryDescription));
        }
        return category;

    }

}
