package com.github.money.keeper.storage;

import com.github.money.keeper.model.RawTransaction;

import java.time.Instant;
import java.util.List;

public interface TransactionRepo {

    void save(Iterable<RawTransaction> transactions);

    List<RawTransaction> loadAll();

    List<RawTransaction> load(Instant from, Instant to);

}
