package com.github.money.keeper.service.impl;

import com.github.money.keeper.model.comparators.Comparators;
import com.github.money.keeper.model.core.*;
import com.github.money.keeper.model.service.BudgetStatistics;
import com.github.money.keeper.model.service.Transaction;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.service.BudgetService;
import com.github.money.keeper.storage.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDate;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

@Service
public class BudgetServiceImpl implements BudgetService {

    private final BudgetRepo budgetRepo;
    private final AccountRepo accountRepo;
    private final CategoryRepo categoryRepo;
    private final TransactionRepo transactionRepo;
    private final SalePointRepo salePointRepo;
    private final StoreRepo storeRepo;

    @Inject
    public BudgetServiceImpl(BudgetRepo budgetRepo,
                             AccountRepo accountRepo,
                             CategoryRepo categoryRepo,
                             TransactionRepo transactionRepo,
                             SalePointRepo salePointRepo,
                             StoreRepo storeRepo,
                             Clock clock) {
        this.accountRepo = accountRepo;
        this.categoryRepo = categoryRepo;
        this.transactionRepo = transactionRepo;
        this.salePointRepo = salePointRepo;
        this.storeRepo = storeRepo;
        this.budgetRepo = budgetRepo;
    }

    @Override
    public BudgetStatistics createBudget(Budget budget) {
        Budget saved = budgetRepo.save(budget);
        return getBudgetStatisticsFor(Collections.singletonList(saved)).get(0);
    }

    @Override
    public List<BudgetStatistics> getBudgetStatistics(LocalDate from, LocalDate to) {
        List<Budget> active = budgetRepo.getAffectedBudgets(from, to);
        return getBudgetStatisticsFor(active);
    }

    private List<BudgetStatistics> getBudgetStatisticsFor(List<Budget> budgets) {
        Set<Long> accountIds = new HashSet<>();
        Set<Long> categoryIds = new HashSet<>();
        LocalDate from = LocalDate.MAX;
        LocalDate to = LocalDate.MIN;
        for (Budget budget : budgets) {
            accountIds.addAll(budget.getAccountIds());
            categoryIds.addAll(budget.getCategoryIds());
            if (budget.getFrom().isBefore(from)) {
                from = budget.getFrom();
            }
            if (budget.getTo().isAfter(to)) {
                to = budget.getTo();
            }
        }
        Map<Long, Account> accountById = accountRepo.get(accountIds).stream()
                .collect(toMap(Account::getId, identity()));
        Map<Long, Category> categoryById = categoryRepo.get(categoryIds).stream()
                .collect(toMap(Category::getId, identity()));
        SortedMap<LocalDate, List<RawTransaction>> transactionsByDate = new TreeMap<>(transactionRepo.load(from, to, accountIds).stream()
                .collect(groupingBy(RawTransaction::getDate)));

        Set<Long> salePointIds = transactionsByDate.values().stream()
                .flatMap(List::stream)
                .map(RawTransaction::getSalePointId)
                .collect(toSet());
        Map<Long, SalePoint> salePointById = salePointRepo.get(salePointIds).stream()
                .collect(toMap(SalePoint::getId, identity()));

        Set<Long> storeIds = salePointById.values().stream()
                .map(SalePoint::getStoreId)
                .collect(toSet());
        Map<Long, Store> storeById = storeRepo.get(storeIds).stream()
                .collect(toMap(Store::getId, identity()));

        return budgets.stream()
                .map(budget -> {
                    List<RawTransaction> transactions = new ArrayList<>();
                    for (LocalDate i = budget.getFrom(); !i.isAfter(budget.getTo()); i = i.plusDays(1)) {
                        List<RawTransaction> dayTx = transactionsByDate.get(i);
                        if (dayTx != null) {
                            dayTx.stream()
                                    .filter(tx -> budget.getAccountIds().contains(tx.getAccountId()))
                                    .filter(tx -> budget.getCategoryIds().contains(
                                            storeById.get(salePointById.get(tx.getSalePointId()).getStoreId()).getCategoryId()
                                    ))
                                    .forEach(transactions::add);
                        }
                    }
                    transactions.sort(Comparators.RawTransactions.natural());
                    return new BudgetStatistics(
                            budget,
                            slice(accountById, budget.getAccountIds()),
                            slice(categoryById, budget.getCategoryIds()),
                            transactions.stream()
                                    .map(rTr -> new Transaction(rTr, salePointById.get(rTr.getSalePointId())))
                                    .map(tr -> new UnifiedTransaction(tr, storeById.get(tr.getSalePoint().getStoreId())))
                                    .collect(toList())
                    );
                })
                .collect(toList());
    }

    private static <K, V> Set<V> slice(Map<K, V> source, Set<K> sliceKeys) {
        Set<V> out = new HashSet<>();
        for (K key : sliceKeys) {
            out.add(source.get(key));
        }
        return out;
    }

}
