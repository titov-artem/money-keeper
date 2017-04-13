package com.github.money.keeper.service;

import com.github.money.keeper.clusterization.merger.action.ActionsCollector;
import com.github.money.keeper.model.core.RawTransaction;

import java.util.List;

public interface StoreService {

    TransactionStoreInjector getStoreInjector(List<RawTransaction> transactions);

    void applyStoreMergeActions(ActionsCollector collector);
}
