package com.github.money.keeper.storage;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.storage.memory.BaseRepo;

import java.time.Instant;
import java.util.List;

public interface TransactionRepo extends BaseRepo<Long, RawTransaction> {

    List<RawTransaction> load(Instant from, Instant to);

}
