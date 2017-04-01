package com.github.money.keeper.model;

import java.util.Objects;

public class CategoryManualMapping {

    private final String storeName;
    private final String categoryName;

    public CategoryManualMapping(String storeName, String categoryName) {
        this.storeName = storeName;
        this.categoryName = categoryName;
    }

    public String getStoreName() {
        return storeName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryManualMapping that = (CategoryManualMapping) o;
        return Objects.equals(storeName, that.storeName) &&
                Objects.equals(categoryName, that.categoryName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeName, categoryName);
    }

    @Override
    public String toString() {
        return "CategoryManualMapping{" +
                "storeName='" + storeName + '\'' +
                ", categoryName='" + categoryName + '\'' +
                '}';
    }
}
