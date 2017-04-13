package com.github.money.keeper.service;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.Store;

import javax.annotation.Nonnull;

public interface CategorizationHelper {

    @Nonnull
    Category determineCategory(Store store);

}
