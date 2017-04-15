package com.github.money.keeper.service;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.service.DuplicateTransactions;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    /**
     * Get duplicated transactions in specified period. Two transaction are duplicates if
     * {@link RawTransaction#isDuplicate(RawTransaction)} return true
     * for them
     *
     * @param from period start inclusively
     * @param to   period end exclusively
     * @return maybe empty sorted list of duplicates. Duplicates will go consecutively
     */
    @Nonnull
    List<DuplicateTransactions> getDuplicates(LocalDate from, LocalDate to);

}
