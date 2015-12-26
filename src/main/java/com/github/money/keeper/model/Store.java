package com.github.money.keeper.model;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

public class Store {

    private final String name;
    private final String categoryDescription;
    private final ImmutableSet<SalePoint> salePoints;

    public Store(String name, String categoryDescription, Set<SalePoint> salePoints) {
        this.name = name;
        this.categoryDescription = categoryDescription;
        this.salePoints = ImmutableSet.copyOf(salePoints);
    }

    public String getName() {
        return name;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public ImmutableSet<SalePoint> getSalePoints() {
        return salePoints;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(name, store.name) &&
                Objects.equals(categoryDescription, store.categoryDescription) &&
                Objects.equals(salePoints, store.salePoints);
    }

    @Override public int hashCode() {
        return Objects.hash(name, categoryDescription, salePoints);
    }
}
