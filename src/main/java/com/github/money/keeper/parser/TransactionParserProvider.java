package com.github.money.keeper.parser;

import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class TransactionParserProvider {

    Map<SupportedParsers, TransactionParser> parsers;

    public TransactionParser getParser(SupportedParsers parserType) {
        TransactionParser transactionParser = parsers.get(parserType);
        if (transactionParser == null) {
            throw new IllegalArgumentException("No parser for type " + parserType + " found");
        }
        return transactionParser;
    }

    @Required
    public void setParsers(Map<SupportedParsers, TransactionParser> parsers) {
        this.parsers = parsers;
    }
}
