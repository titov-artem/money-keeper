package com.github.money.keeper.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class UnifiedTransaction {

    private final RawTransaction rawTransaction;
    private final Store store;

    public UnifiedTransaction(RawTransaction rawTransaction, Store store) {
        this.rawTransaction = rawTransaction;
        this.store = store;
    }

    public LocalDate getDate() {
        return rawTransaction.getDate();
    }

    public BigDecimal getAmount() {
        return rawTransaction.getAmount();
    }

    public SalePoint getSalePoint() {
        return rawTransaction.getSalePoint();
    }

    public Store getStore() {
        return store;
    }

    public RawTransaction getRawTransaction() {
        return rawTransaction;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnifiedTransaction that = (UnifiedTransaction) o;
        return Objects.equals(rawTransaction, that.rawTransaction) &&
                Objects.equals(store, that.store);
    }

    @Override public int hashCode() {
        return Objects.hash(rawTransaction, store);
    }
}
