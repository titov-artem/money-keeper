package com.github.money.keeper.service;

import com.github.money.keeper.model.core.Budget;
import com.github.money.keeper.model.service.BudgetStatistics;

import java.time.LocalDate;
import java.util.List;

public interface BudgetService {
    BudgetStatistics createBudget(Budget budget);

    List<BudgetStatistics> getBudgetStatistics(LocalDate from, LocalDate to);
}
