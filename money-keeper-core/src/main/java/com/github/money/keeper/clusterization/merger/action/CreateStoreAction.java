package com.github.money.keeper.clusterization.merger.action;

import com.github.money.keeper.clusterization.StorePrototype;
import com.github.money.keeper.model.core.AbstractEntity;
import com.github.money.keeper.model.core.Category;
import com.google.common.base.Preconditions;

/**
 * Create store and reassign all specified sale points to it
 */
public class CreateStoreAction implements StoreMergeAction {

    private final StorePrototype prototype;
    private final Long categoryId;

    public CreateStoreAction(StorePrototype prototype, Category category) {
        Preconditions.checkArgument(!AbstractEntity.isFakeId(category.getId()));
        this.prototype = prototype;
        this.categoryId = category.getId();
    }

    public StorePrototype getPrototype() {
        return prototype;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    @Override
    public void apply(ActionsCollector actionsCollector) {
        actionsCollector.collect(this);
    }

}
