package com.github.money.keeper.model.report;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.service.CategoryService;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author Artem Titov
 */
class ReportUtils {

    static Category determineCategory(UnifiedTransaction transaction, Map<String, Category> alternativeToCategory) {
        String categoryDescription = transaction.getStore().getCategoryDescription();
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
