package com.github.money.keeper.service.impl;

import com.github.money.keeper.clusterization.merger.action.ActionsCollector;
import com.github.money.keeper.clusterization.merger.action.AssignPointsAction;
import com.github.money.keeper.clusterization.merger.action.CreateStoreAction;
import com.github.money.keeper.clusterization.merger.action.RenameStoreAction;
import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.model.service.Transaction;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.SalePointRepo;
import com.github.money.keeper.storage.StoreRepo;
import com.github.money.keeper.storage.jdbc.TxHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
public class StoreServiceImpl implements StoreService {

    private final StoreRepo storeRepo;
    private final SalePointRepo salePointRepo;

    private final TxHelper txHelper;

    @Inject
    public StoreServiceImpl(StoreRepo storeRepo,
                            SalePointRepo salePointRepo,
                            TxHelper txHelper) {
        this.storeRepo = storeRepo;
        this.salePointRepo = salePointRepo;
        this.txHelper = txHelper;
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
        assignPointsToStores(collector.getAssignPointsActions());
        renameStores(collector.getRenameStoreActions());
        createStores(collector.getCreateStoreActions());
    }

    private void assignPointsToStores(List<AssignPointsAction> actions) {
        for (AssignPointsAction action : actions) {
            salePointRepo.setStoreId(action.getSalePointIds(), action.getStoreId());
        }
    }

    private void renameStores(List<RenameStoreAction> actions) {
        List<Long> storeIds = new ArrayList<>();
        List<String> storeNames = new ArrayList<>();
        actions.forEach(action -> {
            storeIds.add(action.getStoreId());
            storeNames.add(action.getName());
        });
        storeRepo.batchRename(storeIds, storeNames);
    }

    private void createStores(List<CreateStoreAction> actions) {
        txHelper.withTx(() -> {
            List<Store> stores = actions.stream()
                    .map(action -> new Store(
                            action.getPrototype().getName(),
                            action.getCategoryId()
                    ))
                    .collect(toList());
            List<Store> created = storeRepo.save(stores);
            for (int i = 0; i < created.size(); i++) {
                Set<Long> salePointIds = actions.get(i).getPrototype().getSalePoints().stream()
                        .map(SalePoint::getId)
                        .collect(toSet());
                salePointRepo.setStoreId(salePointIds, created.get(i).getId());
            }
        });
    }

}
