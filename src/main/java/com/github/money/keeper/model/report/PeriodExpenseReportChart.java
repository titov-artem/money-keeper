package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.UnifiedTransaction;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class PeriodExpenseReportChart {

    private final Account account;
    private final LocalDate from;
    private final LocalDate to;
    private final List<CategoryReport> reports;
    private final BigDecimal total;

    private PeriodExpenseReportChart(Account account, LocalDate from, LocalDate to, List<CategoryReport> reports, BigDecimal total) {
        this.account = account;
        this.from = from;
        this.to = to;
        this.reports = reports;
        this.total = total;
    }

    @JsonGetter
    public Account getAccount() {
        return account;
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

        public List<UnifiedTransactionReportView> getTransactions() {
            return transactions;
        }
    }

    public static final class Builder {
        private final Account account;
        private final Map<String, Category> alternativeToCategory = Maps.newHashMap();
        private final Map<Category, CRBuilder> crBuilders = Maps.newHashMap();
        private LocalDate from;
        private LocalDate to;

        public Builder(Account account, List<Category> categories) {
            this.account = account;
            categories.stream().forEach(
                    c -> c.getAlternatives().forEach(
                            a -> Preconditions.checkState(alternativeToCategory.put(a, c) == null, "Duplicated alternative in different categories")
                    )
            );
        }

        public void append(UnifiedTransaction transaction) {
            updateReportPeriod(transaction);
            Category category = ReportUtils.determineCategory(transaction, alternativeToCategory);
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
                    account,
                    from,
                    to,
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
