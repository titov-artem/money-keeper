package com.github.money.keeper.service;

import com.github.money.keeper.clusterization.StoreClusterizer;
import com.github.money.keeper.clusterization.StoreClusterizer.ClusterizationResult;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.TransactionRepo;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Required;

import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public class StoreService {

    private TransactionRepo transactionRepo;
    private StoreClusterizer storeClusterizer;
    private StoreRepo storeRepo;

    /**
     * Recreate store basing on known transactions
     */
    public void rebuildFromTransactionsLog() {
        Set<SalePoint> salePoints = transactionRepo.loadAll().stream().map(RawTransaction::getSalePoint).collect(toSet());
        ClusterizationResult result = storeClusterizer.clusterize(storeRepo.loadAll(), salePoints);
        storeRepo.clear();
        storeRepo.save(result.getStores());
    }

    public TransactionStoreInjector getStoreInjector() {
        Map<SalePoint, Store> pointToStore = storeRepo.loadAll().stream()
                .flatMap(s -> s.getSalePoints().stream().map(p -> Pair.of(p, s)))
                .collect(toMap(Pair::getKey, Pair::getValue));
        return source -> new UnifiedTransaction(source, pointToStore.get(source.getSalePoint()));
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setStoreClusterizer(StoreClusterizer storeClusterizer) {
        this.storeClusterizer = storeClusterizer;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }
}
