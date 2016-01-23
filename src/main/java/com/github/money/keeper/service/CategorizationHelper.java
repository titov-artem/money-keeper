package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.Store;

public interface CategorizationHelper {

    Category determineCategory(Store store);

}
