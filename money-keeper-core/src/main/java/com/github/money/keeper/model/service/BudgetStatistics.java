package com.github.money.keeper.model.service;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.core.Budget;
import com.github.money.keeper.model.core.Category;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class BudgetStatistics {

    private final Budget budget;
    private final Set<Account> accounts;
    private final Set<Category> categories;
    private final List<UnifiedTransaction> transactions;

    public BudgetStatistics(Budget budget,
                            Set<Account> accounts,
                            Set<Category> categories,
                            List<UnifiedTransaction> transactions) {
        this.budget = budget;
        this.accounts = accounts;
        this.categories = categories;
        this.transactions = transactions;
    }

    public Budget getBudget() {
        return budget;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public List<UnifiedTransaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getSpentAmount() {
        return transactions.stream()
                .map(UnifiedTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
