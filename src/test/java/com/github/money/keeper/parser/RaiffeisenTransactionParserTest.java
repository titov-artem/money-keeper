package com.github.money.keeper.parser;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class RaiffeisenTransactionParserTest {

    @Test
    public void testParse() throws Exception {
        try (InputStream in = new FileInputStream("src/test/resources/card-statement-20151226.csv")) {
            ParsingResult result = new RaiffeisenTransactionParser().parse(in);
            System.out.println(result);
        }
    }
}