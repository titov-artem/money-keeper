package com.github.money.keeper.clusterization.merger.action;

import com.github.money.keeper.clusterization.ClusterizableStore;
import com.github.money.keeper.clusterization.StorePrototype;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.core.SalePoint;

public interface StoreMergeAction {

    void apply(ActionsCollector actionsCollector);

    static StoreMergeAction create(StorePrototype prototype, Category category) {
        return new CreateStoreAction(prototype, category);
    }

    static StoreMergeAction assign(ClusterizableStore store, Iterable<SalePoint> salePoints) {
        return new AssignPointsAction(store, salePoints);
    }

    static StoreMergeAction rename(ClusterizableStore store, String name) {
        return new RenameStoreAction(store, name);
    }
}
