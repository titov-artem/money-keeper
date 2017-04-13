package com.github.money.keeper.model.core;

import com.google.common.base.Preconditions;

import java.util.Objects;

public class AbstractEntity<T> {

    protected T id;

    public AbstractEntity(T id) {this.id = id;}

    public T getId() {
        return id;
    }

    public void injectId(T id) {
        Preconditions.checkArgument(!isFakeId(id));
        this.id = id;
    }

    public static <T> T getFakeId() {
        return null;
    }

    public static <T> boolean isFakeId(T id) {
        return id == null;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractEntity<?> that = (AbstractEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override public int hashCode() {
        return Objects.hash(id);
    }
}
