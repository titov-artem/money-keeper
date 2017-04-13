package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.Store;

import java.util.List;
import java.util.Map;

public interface StoreRepo extends BaseRepo<Long, Store> {

    List<Store> findAllByCategory(Long categoryId);

    Map<Long, List<Store>> getAllByCategories();

    void setCategoryId(Iterable<Long> storeIds, Long categoryId);
}
