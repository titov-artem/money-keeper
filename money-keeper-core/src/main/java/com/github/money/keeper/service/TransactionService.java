package com.github.money.keeper.service;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    /**
     * Get duplicated transactions in specified period. Two transaction are duplicates if
     * {@link com.github.money.keeper.model.RawTransaction#isDuplicate(RawTransaction)} return true
     * for them
     *
     * @param from period start inclusively
     * @param to   period end exclusively
     * @return maybe empty sorted list of duplicates. Duplicates will go consecutively
     */
    @Nonnull
    List<UnifiedTransaction> getDuplicates(LocalDate from, LocalDate to);

    /**
     * Remove all duplicates in specified dates range
     *
     * @param from period start inclusively
     * @param to   period end exclusively
     * @return maybe empty list of removed transactions
     */
    @Nonnull
    List<UnifiedTransaction> deduplicate(LocalDate from, LocalDate to);

}
