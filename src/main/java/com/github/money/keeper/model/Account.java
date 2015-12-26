package com.github.money.keeper.model;

import java.util.Objects;

public class Account {

    private final String name;

    public Account(String name) {this.name = name;}

    public String getName() {
        return name;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(name, account.name);
    }

    @Override public int hashCode() {
        return Objects.hash(name);
    }
}
