package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;

import javax.annotation.Nonnull;

public interface CategorizationHelper {

    @Nonnull
    Category determineCategory(Store store);

}
