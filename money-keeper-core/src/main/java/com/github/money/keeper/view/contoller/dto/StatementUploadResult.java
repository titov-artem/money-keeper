package com.github.money.keeper.view.contoller.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.github.money.keeper.model.service.DuplicateTransactions;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StatementUploadResult {

    private Result result;
    private List<DuplicateTransactionView> duplicates;
    private LocalDate from;
    private LocalDate to;

    private StatementUploadResult(Result result, List<DuplicateTransactions> duplicates) {
        this.result = result;
        this.from = LocalDate.MAX;
        this.to = LocalDate.MIN;
        this.duplicates = duplicates.stream()
                .map(DuplicateTransactionView::new)
                .collect(toList());
        duplicates.forEach(t -> {
            if (t.getOrigin().getDate().compareTo(from) < 0) from = t.getOrigin().getDate();
            if (t.getOrigin().getDate().compareTo(to) > 0) to = t.getOrigin().getDate();
        });
    }

    @JsonGetter
    public Result getResult() {
        return result;
    }

    @JsonGetter
    public List<DuplicateTransactionView> getDuplicates() {
        return duplicates;
    }

    @JsonGetter
    public LocalDate getFrom() {
        return from;
    }

    @JsonGetter
    public LocalDate getTo() {
        return to;
    }

    public static StatementUploadResult success(List<DuplicateTransactions> duplicates) {
        return new StatementUploadResult(Result.SUCCESS, duplicates == null ? Collections.emptyList() : duplicates);
    }

    public static StatementUploadResult failed() {
        return new StatementUploadResult(Result.FAILED, Collections.emptyList());
    }

    public static StatementUploadResult noFileChosen() {
        return new StatementUploadResult(Result.NO_FILE_CHOSEN, Collections.emptyList());
    }

    public enum Result {
        SUCCESS, FAILED, NO_FILE_CHOSEN
    }
}
