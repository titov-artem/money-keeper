package com.github.money.keeper.parser;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ParsedTransaction {

    private final Long accountId;
    private final LocalDate date;
    private final String salePointName;
    private final String category;
    private final BigDecimal amount;
    private final String fileHash;
    private final String uploadId;

    private ParsedTransaction(Long accountId,
                              LocalDate date,
                              String salePointName,
                              String category,
                              BigDecimal amount,
                              String fileHash,
                              String uploadId) {
        this.accountId = accountId;
        this.date = date;
        this.salePointName = salePointName;
        this.category = category;
        this.amount = amount;
        this.fileHash = fileHash;
        this.uploadId = uploadId;
    }

    public static ParsedTransaction create(Long accountId,
                                           LocalDate date,
                                           String salePointName,
                                           String category,
                                           BigDecimal amount) {
        return new ParsedTransaction(accountId, date, salePointName, category, amount, null, null);
    }

    public ParsedTransaction withFileInfo(String fileHash, String uploadId) {
        return new ParsedTransaction(accountId, date, salePointName, category, amount, fileHash, uploadId);
    }

    public Long getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getSalePointName() {
        return salePointName;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getFileHash() {
        return fileHash;
    }

    public String getUploadId() {
        return uploadId;
    }
}
