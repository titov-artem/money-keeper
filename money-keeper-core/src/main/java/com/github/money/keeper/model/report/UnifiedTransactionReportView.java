package com.github.money.keeper.model.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.money.keeper.model.service.UnifiedTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * @author Artem Titov
 */
public class UnifiedTransactionReportView {

    @JsonProperty
    public Long id;
    @JsonProperty
    public LocalDate date;
    @JsonProperty
    public String store;
    @JsonProperty
    public BigDecimal amount;

    public UnifiedTransactionReportView(UnifiedTransaction source) {
        this.id = source.getTransaction().getRawTransaction().getId();
        this.date = source.getDate();
        this.store = source.getStore().getName();
        this.amount = source.getAmount();
    }

}
