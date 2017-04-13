package com.github.money.keeper.clusterization.merger;

import com.github.money.keeper.clusterization.ClusterizableStore;
import com.github.money.keeper.clusterization.StorePrototype;
import com.github.money.keeper.clusterization.merger.action.StoreMergeAction;

import java.util.List;

public interface StoresMerger {

    List<StoreMergeAction> merge(Iterable<ClusterizableStore> existingStores, Iterable<StorePrototype> newStores);

}
