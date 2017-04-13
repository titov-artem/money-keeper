package com.github.money.keeper.storage.jdbc;

import java.util.function.Supplier;

public interface TxHelper {

    void withTx(Runnable action);

    <T> T withTx(Supplier<T> action);

}
