package com.github.money.keeper.parser;

public enum SupportedParsers {
    RAIFFEISEN_CARD("Raiffeisen Card Statement", "*.csv"),
    TINKOFF("Tinkoff statement", "*.csv");

    private String name;
    private String fileExtensionPattern;

    SupportedParsers(String name, String fileExtensionPattern) {
        this.name = name;
        this.fileExtensionPattern = fileExtensionPattern;
    }

    public String getSourceName() {
        return name;
    }

    public String getFileExtensionPattern() {
        return fileExtensionPattern;
    }
}
