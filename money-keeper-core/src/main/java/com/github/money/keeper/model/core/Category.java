package com.github.money.keeper.model.core;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

public class Category extends AbstractEntity<Long> {

    private final String name;
    /**
     * Set of category names from raw transactions
     */
    private final ImmutableSet<String> alternatives;

    public Category(String name, Set<String> alternatives) {
        super(getFakeId());
        this.name = name;
        this.alternatives = ImmutableSet.copyOf(alternatives);
    }

    public Category(Long id, String name, Set<String> alternatives) {
        super(id);
        this.name = name;
        this.alternatives = ImmutableSet.copyOf(alternatives);
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<String> getAlternatives() {
        return alternatives;
    }

    public Category rename(String name) {
        return new Category(id, name, alternatives);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name) &&
                Objects.equals(alternatives, category.alternatives);
    }

    @Override public int hashCode() {
        return Objects.hash(super.hashCode(), name, alternatives);
    }

    @Override public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", alternatives=" + alternatives +
                '}';
    }
}
