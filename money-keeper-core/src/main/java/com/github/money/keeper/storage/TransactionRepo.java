package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.RawTransaction;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface TransactionRepo extends BaseRepo<Long, RawTransaction> {

    List<RawTransaction> load(@Nullable LocalDate from, @Nullable LocalDate to);

    List<RawTransaction> load(@Nullable LocalDate from, @Nullable LocalDate to, Set<Integer> accountIds);

}
