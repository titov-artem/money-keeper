package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;
import com.github.money.keeper.service.CategorizationHelper;
import com.github.money.keeper.storage.AccountRepo;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

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
        private final CategorizationHelper categorizationHelper;
        private final AccountRepo accountRepo;
        private final Map<Category, CRBuilder> crBuilders = new HashMap<>();
        private final Set<Integer> accountIds = new HashSet<>();
        private LocalDate from;
        private LocalDate to;

        public Builder(CategorizationHelper categorizationHelper,
                       AccountRepo accountRepo,
                       @Nullable LocalDate from,
                       @Nullable LocalDate to) {
            this.from = from;
            this.to = to;
            this.categorizationHelper = categorizationHelper;
            this.accountRepo = accountRepo;
        }

        public void append(UnifiedTransaction transaction) {
            updateReportPeriod(transaction);
            accountIds.add(transaction.getAccountId());
            Category category = categorizationHelper.determineCategory(transaction.getStore());
            CRBuilder crBuilder = summonBuilder(category);
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
            return new PeriodExpenseReportChart(
                    from,
                    to,
                    accountRepo.load(accountIds).stream().map(Account::getName).collect(toSet()),
                    crBuilders.values().stream()
                            .map(b -> b.build(b.amount.divide(total, 3, RoundingMode.HALF_UP).doubleValue() * 100))
                            .sorted((o1, o2) -> o2.amount.compareTo(o1.amount))
                            .collect(toList()),
                    total);
        }

        private CRBuilder summonBuilder(Category category) {
            CRBuilder out = crBuilders.putIfAbsent(category, new CRBuilder(category));
            return out == null ? crBuilders.get(category) : out;
        }
    }

    private static final class CRBuilder {
        private final Category category;
        private BigDecimal amount = BigDecimal.ZERO;
        private final List<UnifiedTransaction> transactions = Lists.newArrayList();

        private CRBuilder(Category category) {
            this.category = category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void append(UnifiedTransaction transaction) {
            amount = amount.add(transaction.getAmount());
            transactions.add(transaction);
        }

        public CategoryReport build(double percentage) {
            return new CategoryReport(
                    category,
                    amount,
                    percentage,
                    transactions.stream()
                            .sorted((o1, o2) -> o1.getDate().compareTo(o2.getDate()))
                            .map(UnifiedTransactionReportView::new)
                            .collect(toList())
            );
        }
    }
}
