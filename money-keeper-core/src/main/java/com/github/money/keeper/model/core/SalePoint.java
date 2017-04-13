package com.github.money.keeper.model.core;

import javax.annotation.Nullable;
import java.util.Objects;

public class SalePoint extends AbstractEntity<Long> {

    private final String name;
    private final String rawCategory;
    private final @Nullable Long storeId;

    public SalePoint(String name, String rawCategory) {
        super(getFakeId());
        this.name = name;
        this.rawCategory = rawCategory;
        this.storeId = null;
    }

    public SalePoint(Long id, String name, String rawCategory, Long storeId) {
        super(id);
        this.name = name;
        this.rawCategory = rawCategory;
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public String getRawCategory() {
        return rawCategory;
    }

    @Nullable public Long getStoreId() {
        return storeId;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SalePoint salePoint = (SalePoint) o;
        return Objects.equals(name, salePoint.name) &&
                Objects.equals(rawCategory, salePoint.rawCategory) &&
                Objects.equals(storeId, salePoint.storeId);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), name, rawCategory, storeId);
    }

    @Override public String toString() {
        return "SalePoint{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", rawCategory='" + rawCategory + '\'' +
                '}';
    }
}
