package com.github.money.keeper.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class RawTransaction {

    private final Long id;
    private final LocalDate date;
    private final SalePoint salePoint;
    private final BigDecimal amount;

    public RawTransaction(LocalDate date, SalePoint salePoint, BigDecimal amount) {
        this.id = null;
        this.date = date;
        this.salePoint = salePoint;
        this.amount = amount;
    }

    public RawTransaction(Long id, LocalDate date, SalePoint salePoint, BigDecimal amount) {
        this.id = id;
        this.date = date;
        this.salePoint = salePoint;
        this.amount = amount;
    }

    public RawTransaction withId(long id) {
        return new RawTransaction(id, date, salePoint, amount);
    }

    public Long getId() {
        return id;
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
                ", date=" + date +
                ", salePoint=" + salePoint +
                ", amount=" + amount +
                '}';
    }
}
