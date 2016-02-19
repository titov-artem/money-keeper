package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.UnifiedTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Artem Titov
 */
public class UnifiedTransactionReportView {

    private final UnifiedTransaction source;

    public UnifiedTransactionReportView(UnifiedTransaction source) {
        this.source = source;
    }

    @JsonGetter
    public Long getId() {
        return source.getRawTransaction().getId();
    }

    @JsonGetter
    public LocalDate getDate() {
        return source.getDate();
    }

    @JsonGetter
    public String getStore() {
        return source.getStore().getName();
    }

    @JsonGetter
    public BigDecimal getAmount() {
        return source.getAmount();
    }

}
