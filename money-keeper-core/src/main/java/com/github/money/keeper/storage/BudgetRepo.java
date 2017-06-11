package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.Budget;

import java.time.LocalDate;
import java.util.List;

public interface BudgetRepo extends BaseRepo<Long, Budget> {

    List<Budget> getAffectedBudgets(LocalDate from, LocalDate to);
}
