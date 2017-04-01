package com.github.money.keeper.parser;

import org.springframework.beans.factory.annotation.Required;

import java.util.Map;

public class TransactionParserProvider {

    Map<ParserType, AbstractTransactionParser> parsers;

    public AbstractTransactionParser getParser(ParserType parserType) {
        AbstractTransactionParser transactionParser = parsers.get(parserType);
        if (transactionParser == null) {
            throw new IllegalArgumentException("No parser for type " + parserType + " found");
        }
        return transactionParser;
    }

    @Required
    public void setParsers(Map<ParserType, AbstractTransactionParser> parsers) {
        this.parsers = parsers;
    }
}
