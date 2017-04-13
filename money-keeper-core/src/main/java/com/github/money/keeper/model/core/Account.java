package com.github.money.keeper.model.core;

import com.github.money.keeper.parser.ParserType;

import java.util.Objects;

public class Account extends AbstractEntity<Long> {

    private final String name;
    private final ParserType parserType;

    public Account(String name, ParserType parserType) {
        this(getFakeId(), name, parserType);
    }

    public Account(Long id, String name, ParserType parserType) {
        super(id);
        this.name = name;
        this.parserType = parserType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ParserType getParserType() {
        return parserType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(id, account.id) &&
                Objects.equals(name, account.name) &&
                parserType == account.parserType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, parserType);
    }
}
