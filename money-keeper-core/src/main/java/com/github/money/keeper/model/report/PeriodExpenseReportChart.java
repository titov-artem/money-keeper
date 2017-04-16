package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.model.service.UnifiedTransaction;
import com.github.money.keeper.storage.AccountRepo;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.*;

public class PeriodExpenseReportChart {

    private final LocalDate from;
    private final LocalDate to;
    private final Set<String> accountNames;
    private final List<CategoryReport> reports;
    private final BigDecimal total;

    private PeriodExpenseReportChart(LocalDate from,
                                     LocalDate to,
                                     Set<String> accountNames,
                                     List<CategoryReport> reports,
                                     BigDecimal total) {
        this.from = from;
        this.to = to;
        this.accountNames = accountNames;
        this.reports = reports;
        this.total = total;
    }

    @JsonGetter
    public LocalDate getFrom() {
        return from;
    }

    @JsonGetter
    public LocalDate getTo() {
        return to;
    }

    @JsonGetter
    public Set<String> getAccounts() {
        return accountNames;
    }

    @JsonGetter
    public List<CategoryReport> getCategoryReports() {
        return reports;
    }

    @JsonGetter
    public BigDecimal getTotal() {
        return total;
    }

    public static final class CategoryReport {
        private final String id;
        private final Category category;
        private final BigDecimal amount;
        private final double percentage;
        private final List<UnifiedTransactionReportView> transactions;

        private CategoryReport(Category category, BigDecimal amount, double percentage, List<UnifiedTransactionReportView> transactions) {
            this.id = UUID.randomUUID().toString();
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
            this.transactions = transactions;
        }

        @JsonGetter
        public String getId() {
            return id;
        }

        @JsonGetter
        public String getCategory() {
            return category.getName();
        }

        @JsonGetter
        public BigDecimal getAmount() {
            return amount;
        }

        @JsonGetter
        public double getPercentage() {
            return percentage;
        }

        @JsonGetter
        public List<UnifiedTransactionReportView> getTransactions() {
            return transactions;
        }
    }

    public static final class Builder {
        private final AccountRepo accountRepo;
        private final CategoryRepo categoryRepo;
        private final Map<Long, CRBuilder> crBuilders = new HashMap<>();
        private final Set<Long> accountIds = new HashSet<>();
        private LocalDate from;
        private LocalDate to;

        public Builder(AccountRepo accountRepo,
                       CategoryRepo categoryRepo,
                       @Nullable LocalDate from,
                       @Nullable LocalDate to) {
            this.accountRepo = accountRepo;
            this.categoryRepo = categoryRepo;
            this.from = from;
            this.to = to;
        }

        public void append(UnifiedTransaction transaction) {
            updateReportPeriod(transaction);
            accountIds.add(transaction.getAccountId());
            CRBuilder crBuilder = summonBuilder(transaction.getStore().getCategoryId());
            crBuilder.append(transaction);
        }

        private void updateReportPeriod(UnifiedTransaction transaction) {
            if (from == null || from.compareTo(transaction.getDate()) > 0) {
                from = transaction.getDate();
            }
            if (to == null || to.compareTo(transaction.getDate()) < 0) {
                to = transaction.getDate();
            }
        }

        public PeriodExpenseReportChart build() {
            BigDecimal total = crBuilders.values().stream()
                    .map(b -> b.amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            Map<Long, Category> categoryById = categoryRepo.get(crBuilders.keySet()).stream()
                    .collect(toMap(Category::getId, identity()));
            return new PeriodExpenseReportChart(
                    from,
                    to,
                    accountRepo.get(accountIds).stream().map(Account::getName).collect(toSet()),
                    crBuilders.values().stream()
                            .map(b -> b.build(categoryById::get, b.amount.divide(total, 3, RoundingMode.HALF_UP).doubleValue() * 100))
                            .sorted((o1, o2) -> o2.amount.compareTo(o1.amount))
                            .collect(toList()),
                    total);
        }

        private CRBuilder summonBuilder(Long categoryId) {
            CRBuilder out = crBuilders.putIfAbsent(categoryId, new CRBuilder(categoryId));
            return out == null ? crBuilders.get(categoryId) : out;
        }
    }

    private static final class CRBuilder {
        private final Long categoryId;
        private BigDecimal amount = BigDecimal.ZERO;
        private final List<UnifiedTransaction> transactions = Lists.newArrayList();

        private CRBuilder(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getCategoryId() {
            return categoryId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void append(UnifiedTransaction transaction) {
            amount = amount.add(transaction.getAmount());
            transactions.add(transaction);
        }

        public CategoryReport build(Function<Long, Category> categoryProvider, double percentage) {
            return new CategoryReport(
                    categoryProvider.apply(categoryId),
                    amount,
                    percentage,
                    transactions.stream()
                            .sorted(Comparator.comparing(UnifiedTransaction::getDate))
                            .map(UnifiedTransactionReportView::new)
                            .collect(toList())
            );
        }
    }
}
