package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.report.UnifiedTransactionReportView;
import com.github.money.keeper.model.service.DuplicateTransactions;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class DuplicateTransactionView {
    private final UnifiedTransactionReportView origin;
    private final List<UnifiedTransactionReportView> duplicates;

    public DuplicateTransactionView(DuplicateTransactions duplicateTransactions) {
        this.origin = new UnifiedTransactionReportView(duplicateTransactions.getOrigin());
        this.duplicates = duplicateTransactions.getDuplicates().stream()
                .map(UnifiedTransactionReportView::new)
                .collect(toList());
    }

    @JsonGetter
    public UnifiedTransactionReportView getOrigin() {
        return origin;
    }

    @JsonGetter
    public List<UnifiedTransactionReportView> getDuplicates() {
        return duplicates;
    }
}
