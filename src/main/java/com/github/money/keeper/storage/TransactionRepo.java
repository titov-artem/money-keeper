package com.github.money.keeper.storage;

import com.github.money.keeper.model.RawTransaction;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends BaseRepo<Long, RawTransaction> {

    List<RawTransaction> load(@Nullable LocalDate from, @Nullable LocalDate to);

}
