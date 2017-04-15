package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.comparators.Comparators;
import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.model.service.DuplicateTransactions;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.service.TransactionService;
import com.github.money.keeper.service.TransactionStoreInjector;
import com.github.money.keeper.storage.TransactionRepo;
import com.github.money.keeper.util.structure.UnionFind;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
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
    public List<DuplicateTransactions> getDuplicates(LocalDate from, LocalDate to) {
        Map<LocalDate, List<RawTransaction>> transactionsByDate = transactionRepo.load(from, to).stream()
                .collect(groupingBy(RawTransaction::getDate));

        List<List<RawTransaction>> duplicates = Lists.newArrayList();
        for (final Collection<RawTransaction> item : transactionsByDate.values()) {
            List<RawTransaction> curTransactions = item.stream()
                    .sorted(Comparators.RawTransactions.natural())
                    .collect(toList());
            UnionFind<RawTransaction> unionFind = new UnionFind<>(curTransactions);
            for (int i = 0; i < curTransactions.size(); i++) {
                for (int j = i + 1; j < curTransactions.size(); j++) {
                    RawTransaction t1 = curTransactions.get(i);
                    RawTransaction t2 = curTransactions.get(j);
                    if (t1.isDuplicate(t2)) {
                        unionFind.union(t1, t2);
                    }
                }
            }
            duplicates.addAll(
                    unionFind.getClustersAsMap().values().stream()
                            // we need to leave clusters with size only greater than 2
                            .filter(dups -> dups.size() >= 2)
                            .collect(toList())
            );
        }

        TransactionStoreInjector storeInjector = storeService.getStoreInjector(
                duplicates.stream().flatMap(List::stream).collect(toList())
        );
        return duplicates.stream()
                .map(dups -> new DuplicateTransactions(
                        storeInjector.injectStore(dups.get(0)),
                        dups.subList(1, dups.size()).stream().map(storeInjector::injectStore).collect(toList())
                ))
                .collect(toList());
    }
}
