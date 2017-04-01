package com.github.money.keeper.util.io;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class DigestingInputStreamTest {

    @Test
    public void testGetStringDigest() throws Exception {
        String source = "Hello world!";
        DigestingInputStream input = new DigestingInputStream(new ByteArrayInputStream(source.getBytes()));
        try (BufferedReader in = new BufferedReader(new InputStreamReader(input))) {
            StringBuilder actual = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                actual.append(line);
            }
            assertThat(actual.toString(), is(source));
        }
        assertThat(input.getStringDigest(), is("D41D8CD98F00B204E9800998ECF8427E"));
    }
}