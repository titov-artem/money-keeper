package com.github.money.keeper.parser;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class TransactionParserProvider {

    private final Map<ParserType, AbstractTransactionParser> parsers;

    @Inject
    public TransactionParserProvider(List<AbstractTransactionParser> parsers) {
        this.parsers = parsers.stream().collect(toMap(AbstractTransactionParser::getParserType, identity()));
    }

    public AbstractTransactionParser getParser(ParserType parserType) {
        AbstractTransactionParser transactionParser = parsers.get(parserType);
        if (transactionParser == null) {
            throw new IllegalArgumentException("No parser for type " + parserType + " found");
        }
        return transactionParser;
    }

}
