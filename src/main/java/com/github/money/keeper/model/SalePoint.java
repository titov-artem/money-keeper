package com.github.money.keeper.model;

import java.util.Objects;

public class SalePoint {

    private final String name;
    private final String description;

    public SalePoint(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalePoint salePoint = (SalePoint) o;
        return Objects.equals(name, salePoint.name) &&
                Objects.equals(description, salePoint.description);
    }

    @Override public int hashCode() {
        return Objects.hash(name, description);
    }

    @Override public String toString() {
        return "SalePoint{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
