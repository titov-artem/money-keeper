package com.github.money.keeper.model.service;

import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.model.core.Store;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class UnifiedTransaction {

    private final Transaction transaction;
    private final Store store;

    public UnifiedTransaction(Transaction transaction, Store store) {
        this.transaction = transaction;
        this.store = store;
    }

    public Long getId() {
        return transaction.getRawTransaction().getId();
    }

    public Long getAccountId() {
        return transaction.getRawTransaction().getAccountId();
    }

    public LocalDate getDate() {
        return transaction.getRawTransaction().getDate();
    }

    public BigDecimal getAmount() {
        return transaction.getRawTransaction().getAmount();
    }

    public SalePoint getSalePoint() {
        return transaction.getSalePoint();
    }

    public Store getStore() {
        return store;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnifiedTransaction that = (UnifiedTransaction) o;
        return Objects.equals(transaction, that.transaction) &&
                Objects.equals(store, that.store);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transaction, store);
    }
}
