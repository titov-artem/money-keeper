package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.report.UnifiedTransactionReportView;
import com.github.money.keeper.model.service.BudgetStatistics;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

public class BudgetView {

    @JsonProperty
    public Set<AccountDto> accounts;
    @JsonProperty
    public Set<CategoryDto> categories;
    @JsonProperty
    public String name;
    @JsonProperty
    public LocalDate from;
    @JsonProperty
    public LocalDate to;
    @JsonProperty
    public BigDecimal amount;

    @JsonProperty
    public BigDecimal spentAmount;
    @JsonProperty
    public BigDecimal progress;
    @JsonProperty
    public List<UnifiedTransactionReportView> transactions;

    public BudgetView() {
    }

    public BudgetView(BudgetStatistics stat) {
        this.accounts = stat.getAccounts().stream().map(AccountDto::new).collect(toSet());
        this.categories = stat.getCategories().stream().map(CategoryDto::new).collect(toSet());

        this.name = stat.getBudget().getName();
        this.from = stat.getBudget().getFrom();
        this.to = stat.getBudget().getTo();
        this.amount = stat.getBudget().getAmount();

        this.spentAmount = stat.getSpentAmount();
        this.progress = stat.getSpentAmount()
                .divide(stat.getBudget().getAmount(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
        this.transactions = stat.getTransactions().stream()
                .map(UnifiedTransactionReportView::new)
                .collect(toList());
    }
}
