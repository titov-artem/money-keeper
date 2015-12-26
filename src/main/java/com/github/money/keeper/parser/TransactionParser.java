package com.github.money.keeper.parser;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parse bank statement into application data model
 */
public interface TransactionParser {

    ParsingResult parse(InputStream source) throws IOException;

}
