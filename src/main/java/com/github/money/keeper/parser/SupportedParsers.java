package com.github.money.keeper.parser;

/**
 * Created by GhostInTheShell on 05.01.16.
 */
public enum SupportedParsers {
    RAIFFEISEN_CARD("Raiffeisen Card Statement");

    private String name;

    SupportedParsers(String name) {
        this.name = name;
    }

    public String getSourceName() {
        return name;
    }
}
