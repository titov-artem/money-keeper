package com.github.money.keeper.model;

import java.util.Objects;

public class SalePoint {

    private final String name;
    private final String categoryDescription;

    public SalePoint(String name, String categoryDescription) {
        this.name = name;
        this.categoryDescription = categoryDescription;
    }

    public String getName() {
        return name;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalePoint salePoint = (SalePoint) o;
        return Objects.equals(name, salePoint.name) &&
                Objects.equals(categoryDescription, salePoint.categoryDescription);
    }

    @Override public int hashCode() {
        return Objects.hash(name, categoryDescription);
    }

    @Override public String toString() {
        return "SalePoint{" +
                "name='" + name + '\'' +
                ", categoryDescription='" + categoryDescription + '\'' +
                '}';
    }
}
