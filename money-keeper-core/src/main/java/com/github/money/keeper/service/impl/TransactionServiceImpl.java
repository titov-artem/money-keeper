package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.model.comparators.Comparators;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class TransactionServiceImpl implements TransactionService {

    private TransactionRepo transactionRepo;
    private StoreService storeService;

    @Nonnull
    @Override
    public List<UnifiedTransaction> getDuplicates(LocalDate from, LocalDate to) {
        Map<LocalDate, List<RawTransaction>> transactionsByDate = transactionRepo.load(from, to).stream()
                .collect(groupingBy(RawTransaction::getDate));

        List<RawTransaction> duplicates = Lists.newArrayList();
        for (final Collection<RawTransaction> item : transactionsByDate.values()) {
            List<RawTransaction> curTransactions = item.stream()
                    .sorted(Comparators.Transactions.natural())
                    .collect(toList());
            BitSet added = new BitSet(curTransactions.size());
            for (int i = 0; i < curTransactions.size(); i++) {
                for (int j = i + 1; j < curTransactions.size(); j++) {
                    RawTransaction t1 = curTransactions.get(i);
                    RawTransaction t2 = curTransactions.get(j);
                    if (t1.isDuplicate(t2)) {
                        if (!added.get(i)) {
                            duplicates.add(t1);
                            added.set(i);
                        }
                        if (!added.get(j)) {
                            duplicates.add(t2);
                            added.set(j);
                        }
                    }
                }
            }
        }

        TransactionStoreInjector storeInjector = storeService.getStoreInjector();
        return duplicates.stream()
                .map(storeInjector::injectStore)
                .collect(toList());
    }

    @Nonnull
    @Override
    public List<UnifiedTransaction> deduplicate(LocalDate from, LocalDate to) {
        List<UnifiedTransaction> duplicates = getDuplicates(from, to);
        UnifiedTransaction cur = null;
        List<UnifiedTransaction> removed = Lists.newArrayList();
        for (final UnifiedTransaction transaction : duplicates) {
            if (cur == null || !isDuplicates(cur, transaction)) {
                cur = transaction;
            } else if (isDuplicates(cur, transaction)) {
                transactionRepo.delete(transaction.getId());
                removed.add(transaction);
            }
        }
        return removed;
    }

    private boolean isDuplicates(UnifiedTransaction cur, UnifiedTransaction transaction) {
        return cur.getRawTransaction().isDuplicate(transaction.getRawTransaction());
    }

    @Required
    public void setTransactionRepo(TransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    @Required
    public void setStoreService(StoreService storeService) {
        this.storeService = storeService;
    }
}
