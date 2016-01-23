package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

@Deprecated
public class AutomaticOnlyCategorizationHelper implements CategorizationHelper {

    private final Map<String, Category> alternativeToCategory = Maps.newHashMap();

    public AutomaticOnlyCategorizationHelper(List<Category> categories) {
        categories.stream().forEach(
                c -> c.getAlternatives().forEach(
                        a -> Preconditions.checkState(alternativeToCategory.put(a, c) == null, "Duplicated alternative in different categories")
                )
        );
    }

    @Override
    public Category determineCategory(Store store) {
        String categoryDescription = store.getCategoryDescription();
        if (StringUtils.isBlank(categoryDescription)) {
            categoryDescription = CategoryService.UNKNOWN_CATEGORY_NAME;
        }
        Category category = alternativeToCategory.get(categoryDescription);
        if (category == null) {
            category = new Category(categoryDescription, ImmutableSet.of(categoryDescription));
        }
        return category;
    }

}

