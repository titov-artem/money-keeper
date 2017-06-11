package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

public class BudgetForm {

    @JsonProperty
    public Set<Long> accountIds;
    @JsonProperty
    public Set<Long> categoryIds;
    @JsonProperty
    public String name;
    @JsonProperty
    public LocalDate from;
    @JsonProperty
    public LocalDate to;
    @JsonProperty
    public BigDecimal amount;

}
