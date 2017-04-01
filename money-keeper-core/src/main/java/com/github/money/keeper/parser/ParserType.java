package com.github.money.keeper.parser;

public enum ParserType {
    RAIFFEISEN_CARD("Raiffeisen Card", "*.csv", "yellow"),
    TINKOFF("Tinkoff", "*.csv", "white");

    private String name;
    private String fileExtensionPattern;
    private String color;

    ParserType(String name, String fileExtensionPattern, String color) {
        this.name = name;
        this.fileExtensionPattern = fileExtensionPattern;
        this.color = color;
    }

    public String getSourceName() {
        return name;
    }

    public String getFileExtensionPattern() {
        return fileExtensionPattern;
    }

    public String getColor() {
        return color;
    }
}
