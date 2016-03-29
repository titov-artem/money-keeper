package com.github.money.keeper.parser;

import com.github.money.keeper.model.Account;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class RaiffeisenTransactionParserTest {

    @Test
    public void testParse() throws Exception {
        try (InputStream in = new FileInputStream("src/test/resources/card-statement-20151226.csv")) {
            ParsingResult result = new RaiffeisenTransactionParser().parse(new Account(1, "1234", ParserType.RAIFFEISEN_CARD), in);
            System.out.println(result);
        }
    }
}