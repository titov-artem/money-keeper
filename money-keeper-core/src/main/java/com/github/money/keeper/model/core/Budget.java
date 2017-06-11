package com.github.money.keeper.model.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

/**
 * Describe a budget on selected period for specified accounts and categories
 */
public class Budget extends AbstractEntity<Long> {

    private Set<Long> accountIds;
    private Set<Long> categoryIds;
    private String name;
    private LocalDate from;
    private LocalDate to;
    private BigDecimal amount;

    public Budget(Long id,
                  Set<Long> accountIds,
                  Set<Long> categoryIds,
                  String name,
                  LocalDate from,
                  LocalDate to,
                  BigDecimal amount) {
        super(id);
        this.accountIds = accountIds;
        this.categoryIds = categoryIds;
        this.name = name;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Budget(Set<Long> accountIds,
                  Set<Long> categoryIds,
                  String name,
                  LocalDate from,
                  LocalDate to,
                  BigDecimal amount) {
        super(getFakeId());
        this.accountIds = accountIds;
        this.categoryIds = categoryIds;
        this.name = name;
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Set<Long> getAccountIds() {
        return Collections.unmodifiableSet(accountIds);
    }

    public Set<Long> getCategoryIds() {
        return Collections.unmodifiableSet(categoryIds);
    }

    public String getName() {
        return name;
    }

    public LocalDate getFrom() {
        return from;
    }

    public LocalDate getTo() {
        return to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Budget budget = (Budget) o;
        return Objects.equals(accountIds, budget.accountIds) &&
                Objects.equals(categoryIds, budget.categoryIds) &&
                Objects.equals(name, budget.name) &&
                Objects.equals(from, budget.from) &&
                Objects.equals(to, budget.to) &&
                Objects.equals(amount, budget.amount);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), accountIds, categoryIds, name, from, to, amount);
    }
}
