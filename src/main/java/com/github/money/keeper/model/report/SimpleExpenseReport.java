package com.github.money.keeper.model.report;

import com.github.money.keeper.model.Account;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.model.UnifiedTransaction;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

public class SimpleExpenseReport {

    private final Account account;
    private final List<CategoryReport> reports;
    private final BigDecimal total;

    public SimpleExpenseReport(Account account, List<CategoryReport> reports, BigDecimal total) {
        this.account = account;
        this.reports = reports;
        this.total = total;
    }

    public Account getAccount() {
        return account;
    }

    public List<CategoryReport> getReports() {
        return reports;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public static final class CategoryReport {
        private final Category category;
        private final BigDecimal amount;
        private final double percentage;
        private final List<RawTransaction> transactions;

        public CategoryReport(Category category, BigDecimal amount, double percentage, List<RawTransaction> transactions) {
            this.category = category;
            this.amount = amount;
            this.percentage = percentage;
            this.transactions = transactions;
        }

        public Category getCategory() {
            return category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public double getPercentage() {
            return percentage;
        }

        public List<RawTransaction> getTransactions() {
            return transactions;
        }
    }

    public static final class Builder {
        private final Account account;
        private final List<Category> categories;
        private final Map<String, Category> alternativeToCategory = Maps.newHashMap();
        private final Map<Category, CRBuilder> crBuilders = Maps.newHashMap();

        public Builder(Account account, List<Category> categories) {
            this.account = account;
            this.categories = categories;
            categories.stream().forEach(
                    c -> c.getAlternatives().forEach(
                            a -> Preconditions.checkState(alternativeToCategory.put(a, c) == null, "Duplicated alternative in different categories")
                    )
            );
        }

        public void append(UnifiedTransaction transaction) {
            Category category = determineCategory(transaction);
            CRBuilder crBuilder = summonBuilder(category);
            crBuilder.append(transaction.getRawTransaction());
        }

        public SimpleExpenseReport build() {
            BigDecimal total = crBuilders.values().stream()
                    .map(b -> b.amount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return new SimpleExpenseReport(
                    account,
                    crBuilders.values().stream()
                            .map(b -> b.build(b.amount.divide(total, 2, RoundingMode.HALF_UP).doubleValue() * 100))
                            .sorted((o1, o2) -> o2.amount.compareTo(o1.amount))
                            .collect(toList()),
                    total);
        }

        private Category determineCategory(UnifiedTransaction transaction) {
            String categoryDescription = transaction.getStore().getCategoryDescription();
            if (StringUtils.isBlank(categoryDescription)) {
                categoryDescription = "Unknown";
            }
            Category category = alternativeToCategory.get(categoryDescription);
            if (category == null) {
                category = new Category(categoryDescription, ImmutableSet.of(categoryDescription));
            }
            return category;
        }

        private CRBuilder summonBuilder(Category category) {
            CRBuilder out = crBuilders.putIfAbsent(category, new CRBuilder(category));
            return out == null ? crBuilders.get(category) : out;
        }
    }

    private static final class CRBuilder {
        private final Category category;
        private BigDecimal amount = BigDecimal.ZERO;
        private final List<RawTransaction> transactions = Lists.newArrayList();

        public CRBuilder(Category category) {
            this.category = category;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void append(RawTransaction transaction) {
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
                            .collect(toList())
            );
        }
    }
}
