package com.github.money.keeper.storage;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.storage.memory.BaseRepo;
import com.sun.istack.internal.Nullable;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends BaseRepo<Long, RawTransaction> {

    List<RawTransaction> load(@Nullable LocalDate from, @Nullable LocalDate to);

}
