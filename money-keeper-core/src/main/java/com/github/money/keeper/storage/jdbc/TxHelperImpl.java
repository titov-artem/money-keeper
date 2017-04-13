package com.github.money.keeper.storage.jdbc;

import org.springframework.transaction.support.TransactionOperations;

import java.util.function.Supplier;

public class TxHelperImpl implements TxHelper {

    private TransactionOperations transactionOperations;

    public TxHelperImpl(TransactionOperations transactionOperations) {
        this.transactionOperations = transactionOperations;
    }

    @Override public void withTx(Runnable action) {
        transactionOperations.execute((status) -> {
            action.run();
            return null;
        });
    }

    @Override public <T> T withTx(Supplier<T> action) {
        return transactionOperations.execute(status -> action.get());
    }
}
