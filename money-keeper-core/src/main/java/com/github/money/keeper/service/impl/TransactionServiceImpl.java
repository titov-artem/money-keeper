package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.comparators.Comparators;
import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final StoreService storeService;

    @Inject
    public TransactionServiceImpl(TransactionRepo transactionRepo, StoreService storeService) {
        this.transactionRepo = transactionRepo;
        this.storeService = storeService;
    }

    @Nonnull
    @Override
    public List<UnifiedTransaction> getDuplicates(LocalDate from, LocalDate to) {
        Map<LocalDate, List<RawTransaction>> transactionsByDate = transactionRepo.load(from, to).stream()
                .collect(groupingBy(RawTransaction::getDate));

        List<RawTransaction> duplicates = Lists.newArrayList();
        for (final Collection<RawTransaction> item : transactionsByDate.values()) {
            List<RawTransaction> curTransactions = item.stream()
                    .sorted(Comparators.RawTransactions.natural())
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

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(duplicates);
        return duplicates.stream()
                .map(storeInjector::injectStore)
                .collect(toList());
    }
}
