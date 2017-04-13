package com.github.money.keeper.model.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RawTransaction extends AbstractEntity<Long> {

    private final Long accountId;
    private final LocalDate date;
    private final Long salePointId;
    private final BigDecimal amount;
    private final String fileHash;
    private final String uploadId;

    public RawTransaction(Long accountId,
                          LocalDate date,
                          Long salePointId,
                          BigDecimal amount,
                          String fileHash,
                          String uploadId) {
        super(getFakeId());
        this.accountId = accountId;
        this.date = date;
        this.salePointId = salePointId;
        this.amount = amount;
        this.fileHash = fileHash;
        this.uploadId = uploadId;
    }

    public RawTransaction(Long id,
                          Long accountId,
                          LocalDate date,
                          Long salePointId,
                          BigDecimal amount,
                          String fileHash,
                          String uploadId) {
        super(id);
        this.accountId = accountId;
        this.date = date;
        this.salePointId = salePointId;
        this.amount = amount;
        this.fileHash = fileHash;
        this.uploadId = uploadId;
    }

    public Long getId() {
        return id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getSalePointId() {
        return salePointId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return hash of file, from which transaction was uploaded
     */
    public String getFileHash() {
        return fileHash;
    }

    /**
     * @return unique id of upload when transaction was added to system
     */
    public String getUploadId() {
        return uploadId;
    }

    public boolean isDuplicate(RawTransaction that) {
        boolean contentIsSimilar = Objects.equals(accountId, that.accountId)
                && Objects.equals(date, that.date)
                && Objects.equals(salePointId, that.salePointId)
                && Objects.equals(amount, that.amount);
        boolean isDifferentUploads = !Objects.equals(uploadId, that.uploadId);
        return contentIsSimilar && isDifferentUploads;
    }

    @Override
    public String toString() {
        return "RawTransaction{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", date=" + date +
                ", salePoint=" + salePointId +
                ", amount=" + amount +
                ", fileHash='" + fileHash + '\'' +
                ", uploadId='" + uploadId + '\'' +
                '}';
    }
}
