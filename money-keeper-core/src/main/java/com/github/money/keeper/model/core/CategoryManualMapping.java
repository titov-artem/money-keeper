package com.github.money.keeper.model.core;

import java.util.Objects;

public class CategoryManualMapping {

    private final Long storeId;
    private final Long categoryId;

    public CategoryManualMapping(Long storeId, Long categoryId) {
        this.storeId = storeId;
        this.categoryId = categoryId;
    }

    public Long getstoreId() {
        return storeId;
    }

    public Long getcategoryId() {
        return categoryId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryManualMapping that = (CategoryManualMapping) o;
        return Objects.equals(storeId, that.storeId) &&
                Objects.equals(categoryId, that.categoryId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, categoryId);
    }

    @Override
    public String toString() {
        return "CategoryManualMapping{" +
                "storeId='" + storeId + '\'' +
                ", categoryId='" + categoryId + '\'' +
                '}';
    }
}
