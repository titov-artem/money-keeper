package com.github.money.keeper.service;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.service.UnifiedTransaction;

public interface TransactionStoreInjector {

    UnifiedTransaction injectStore(RawTransaction source);

}
