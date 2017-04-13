package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.model.core.Store;
import com.google.common.base.Preconditions;

import java.util.Objects;
import java.util.Set;

public class ClusterizableStore {
    private final Store store;
    private final Category category;
    private final Set<SalePoint> salePoints;

    public ClusterizableStore(Store store, Category category, Set<SalePoint> salePoints) {
        Preconditions.checkArgument(Objects.equals(store.getCategoryId(), category.getId()));
        this.store = store;
        this.category = category;
        this.salePoints = salePoints;
    }

    public Store getStore() {
        return store;
    }

    public Category getCategory() {
        return category;
    }

    public Set<SalePoint> getSalePoints() {
        return salePoints;
    }

    public boolean isManuallyCreated() {
        return store.isManuallyCreated();
    }

    public String getName() {
        return store.getName();
    }
}
