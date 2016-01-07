package com.github.money.keeper.service;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.service.ClusterizationService.ClusterizationResult;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.TransactionRepo;
import org.springframework.beans.factory.annotation.Required;

import java.util.Collections;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

public class StoreService {

    private TransactionRepo transactionRepo;
    private ClusterizationService clusterizationService;
    private StoreRepo storeRepo;

    /**
     * Recreate store basing on known transactions
     */
    public void rebuildFromTransactionsLog() {
        // TODO here we need to use old known stores to provide ability to specify store for sale point
        Set<SalePoint> salePoints = transactionRepo.loadAll().stream().map(RawTransaction::getSalePoint).collect(toSet());
        ClusterizationResult result = clusterizationService.clusterize(Collections.emptyList(), salePoints);
        storeRepo.clear();
        storeRepo.save(result.getStores());
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setClusterizationService(ClusterizationService clusterizationService) {
        this.clusterizationService = clusterizationService;
    }

    @Required
    public void setStoreRepo(StoreRepo storeRepo) {
        this.storeRepo = storeRepo;
    }
}
