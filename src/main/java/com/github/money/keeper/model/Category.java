package com.github.money.keeper.model;

import com.google.common.collect.ImmutableSet;

import java.util.Objects;
import java.util.Set;

public class Category {

    private final String name;
    private final ImmutableSet<String> alternatives;

    public Category(String name, Set<String> alternatives) {
        this.name = name;
        this.alternatives = ImmutableSet.copyOf(alternatives);
    }

    public String getName() {
        return name;
    }

    public ImmutableSet<String> getAlternatives() {
        return alternatives;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(name, category.name)
                && Objects.equals(alternatives, category.alternatives);
    }

    @Override public int hashCode() {
        return Objects.hash(name, alternatives);
    }
}
