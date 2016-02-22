package com.github.money.keeper.parser;

import com.github.money.keeper.model.Account;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class TinkoffTransactionParserTest {

    @Test
    public void testParse() throws Exception {
        try (InputStream in = new FileInputStream("src/test/resources/tinkoff.csv")) {
            ParsingResult result = new TinkoffTransactionParser().parse(new Account(1L, "1234", ParserType.TINKOFF), in);
            System.out.println(result);
        }
    }
}