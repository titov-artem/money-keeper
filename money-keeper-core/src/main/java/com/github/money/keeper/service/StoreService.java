package com.github.money.keeper.service;

public interface StoreService {
    void rebuildFromTransactionsLog();

    TransactionStoreInjector getStoreInjector();
}
