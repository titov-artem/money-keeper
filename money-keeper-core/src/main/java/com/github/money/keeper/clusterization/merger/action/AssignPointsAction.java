package com.github.money.keeper.clusterization.merger.action;

import com.github.money.keeper.clusterization.ClusterizableStore;
import com.github.money.keeper.model.core.SalePoint;

import java.util.Collections;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

/**
 * Reassign sale points to specified store
 */
public class AssignPointsAction implements StoreMergeAction {

    private final Long storeId;
    private final List<Long> salePointIds;

    public AssignPointsAction(ClusterizableStore store, Iterable<SalePoint> salePoints) {
        this.storeId = store.getStore().getId();
        this.salePointIds = StreamSupport.stream(salePoints.spliterator(), false)
                .map(SalePoint::getId)
                .collect(toList());
    }

    public Long getStoreId() {
        return storeId;
    }

    public List<Long> getSalePointIds() {
        return Collections.unmodifiableList(salePointIds);
    }

    @Override
    public void apply(ActionsCollector actionsCollector) {
        actionsCollector.collect(this);
    }
}
