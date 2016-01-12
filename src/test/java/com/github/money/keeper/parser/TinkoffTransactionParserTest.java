package com.github.money.keeper.parser;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class TinkoffTransactionParserTest {

    @Test
    public void testParse() throws Exception {
        try (InputStream in = new FileInputStream("src/test/resources/tinkoff.csv")) {
            ParsingResult result = new TinkoffTransactionParser().parse(in);
            System.out.println(result);
        }
    }
}