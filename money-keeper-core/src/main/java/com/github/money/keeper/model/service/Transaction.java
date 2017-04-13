package com.github.money.keeper.model.service;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.core.SalePoint;

import java.util.Objects;

public class Transaction {

    private final RawTransaction rawTransaction;
    private final SalePoint salePoint;

    public Transaction(RawTransaction rawTransaction, SalePoint salePoint) {
        this.rawTransaction = rawTransaction;
        this.salePoint = salePoint;
    }

    public RawTransaction getRawTransaction() {
        return rawTransaction;
    }

    public SalePoint getSalePoint() {
        return salePoint;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(rawTransaction, that.rawTransaction) &&
                Objects.equals(salePoint, that.salePoint);
    }

    @Override public int hashCode() {
        return Objects.hash(rawTransaction, salePoint);
    }

    @Override public String toString() {
        return "Transaction{" +
                "rawTransaction=" + rawTransaction +
                ", salePoint=" + salePoint +
                '}';
    }
}
