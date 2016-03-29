package com.github.money.keeper.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RawTransaction {

    private final Long id;
    private final Integer accountId;
    private final LocalDate date;
    private final SalePoint salePoint;
    private final BigDecimal amount;
    private final String fileHash;
    private final String uploadId;

    public RawTransaction(Integer accountId, LocalDate date, SalePoint salePoint, BigDecimal amount) {
        this.id = null;
        this.accountId = accountId;
        this.date = date;
        this.salePoint = salePoint;
        this.amount = amount;
        this.fileHash = null;
        this.uploadId = null;
    }

    public RawTransaction(Long id,
                          Integer accountId,
                          LocalDate date,
                          SalePoint salePoint,
                          BigDecimal amount,
                          String fileHash,
                          String uploadId) {
        this.id = id;
        this.accountId = accountId;
        this.date = date;
        this.salePoint = salePoint;
        this.amount = amount;
        this.fileHash = fileHash;
        this.uploadId = uploadId;
    }

    public RawTransaction withId(long id) {
        return new RawTransaction(id, accountId, date, salePoint, amount, fileHash, uploadId);
    }

    public RawTransaction withFileInfo(String fileHash, String uploadId) {
        return new RawTransaction(id, accountId, date, salePoint, amount, fileHash, uploadId);
    }

    public Long getId() {
        return id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public LocalDate getDate() {
        return date;
    }

    public SalePoint getSalePoint() {
        return salePoint;
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
                && Objects.equals(salePoint, that.salePoint)
                && Objects.equals(amount, that.amount);
        boolean isDifferentUploads = !Objects.equals(uploadId, that.uploadId);
        return contentIsSimilar && isDifferentUploads;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawTransaction that = (RawTransaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "RawTransaction{" +
                "id=" + id +
                ", accountId=" + accountId +
                ", date=" + date +
                ", salePoint=" + salePoint +
                ", amount=" + amount +
                ", fileHash='" + fileHash + '\'' +
                ", uploadId='" + uploadId + '\'' +
                '}';
    }
}
