package com.github.money.keeper.clusterization.merger.action;

import com.github.money.keeper.clusterization.ClusterizableStore;

/**
 * Update store name
 */
public class RenameStoreAction implements StoreMergeAction {

    private final Long storeId;
    private final String name;

    public RenameStoreAction(ClusterizableStore store, String name) {
        this.storeId = store.getStore().getId();
        this.name = name;
    }

    public Long getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    @Override
    public void apply(ActionsCollector actionsCollector) {
        actionsCollector.collect(this);
    }

}
