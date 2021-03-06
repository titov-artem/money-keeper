package com.github.money.keeper.parser;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.util.io.DigestingInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Parse bank statement into application data model
 */
public abstract class AbstractTransactionParser {

    public ParsingResult parse(Account account, Category defaultCategory, InputStream source) throws IOException {
        DigestingInputStream digestStream = new DigestingInputStream(source);
        ParsingResult result = doParse(account, defaultCategory, digestStream);
        String uploadId = UUID.randomUUID().toString();
        return new ParsingResult(
                result.getAccount(),
                result.getTransactions().stream()
                        .map(t -> t.withFileInfo(digestStream.getStringDigest(), uploadId))
                        .collect(toList())
        );
    }

    protected abstract ParsingResult doParse(Account account, Category defaultCategory, InputStream source) throws IOException;

    protected abstract ParserType getParserType();

}
