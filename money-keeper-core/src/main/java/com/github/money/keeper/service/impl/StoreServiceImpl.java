package com.github.money.keeper.service.impl;

import com.github.money.keeper.clusterization.merger.action.ActionsCollector;
import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.model.service.Transaction;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.SalePointRepo;
import com.github.money.keeper.storage.StoreRepo;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class StoreServiceImpl implements StoreService {

    private final StoreRepo storeRepo;
    private final SalePointRepo salePointRepo;

    @Inject
    public StoreServiceImpl(StoreRepo storeRepo, SalePointRepo salePointRepo) {
        this.storeRepo = storeRepo;
        this.salePointRepo = salePointRepo;
    }

    @Override
    public TransactionStoreInjector getStoreInjector(List<RawTransaction> transactions) {
        Set<Long> salePointIds = transactions.stream()
                .map(RawTransaction::getSalePointId)
                .collect(toSet());
        Map<Long, SalePoint> salePointById = salePointRepo.get(salePointIds).stream()
                .collect(toMap(SalePoint::getId, identity()));
        Set<Long> storeIds = salePointById.values().stream()
                .map(SalePoint::getStoreId)
                .collect(toSet());
        Map<Long, Store> storeById = storeRepo.get(storeIds).stream()
                .collect(toMap(Store::getId, identity()));

        return source -> {
            SalePoint salePoint = salePointById.get(source.getSalePointId());
            Store store = storeById.get(salePoint.getStoreId());
            return new UnifiedTransaction(
                    new Transaction(source, salePoint),
                    store
            );
        };
    }

    @Override
    public void applyStoreMergeActions(ActionsCollector collector) {
        // todo
    }

}
