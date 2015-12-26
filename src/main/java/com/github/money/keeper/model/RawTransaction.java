package com.github.money.keeper.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RawTransaction {

    private final LocalDate date;
    private final SalePoint salePoint;
    private final BigDecimal amount;

    public RawTransaction(LocalDate date, SalePoint salePoint, BigDecimal amount) {
        this.date = date;
        this.salePoint = salePoint;
        this.amount = amount;
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

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawTransaction that = (RawTransaction) o;
        return Objects.equals(date, that.date) &&
                Objects.equals(salePoint, that.salePoint) &&
                Objects.equals(amount, that.amount);
    }

    @Override public int hashCode() {
        return Objects.hash(date, salePoint, amount);
    }

    @Override public String toString() {
        return "RawTransaction{" +
                "date=" + date +
                ", salePoint=" + salePoint +
                ", amount=" + amount +
                '}';
    }
}
