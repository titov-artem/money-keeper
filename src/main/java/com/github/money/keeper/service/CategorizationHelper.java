package com.github.money.keeper.service;

import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;

public interface CategorizationHelper {

    Category determineCategory(UnifiedTransaction transaction);

}
