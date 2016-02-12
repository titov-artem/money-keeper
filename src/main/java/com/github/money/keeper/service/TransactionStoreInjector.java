package com.github.money.keeper.service;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;

public interface TransactionStoreInjector {

    UnifiedTransaction injectStore(RawTransaction source);

}
