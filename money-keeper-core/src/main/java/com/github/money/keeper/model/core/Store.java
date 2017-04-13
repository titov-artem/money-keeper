package com.github.money.keeper.model.core;

public class Store extends AbstractEntity<Long> {

    private final String name;
    private final Long categoryId;

    public Store(String name, Long categoryId) {
        super(getFakeId());
        this.name = name;
        this.categoryId = categoryId;
    }

    public Store(Long id, String name, Long categoryId) {
        super(id);
        this.name = name;
        this.categoryId = categoryId;
    }

    public Store withName(String name) {
        return new Store(id, name, categoryId);
    }

    public Store withCategory(Category category) {
        return new Store(id, name, category.getId());
    }

    public String getName() {
        return name;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    // TODO: support manual store creation from sale points
    public boolean isManuallyCreated() {
        return false;
    }

    @Override
    public String toString() {
        return "Store{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + categoryId + '\'' +
                '}';
    }
}
