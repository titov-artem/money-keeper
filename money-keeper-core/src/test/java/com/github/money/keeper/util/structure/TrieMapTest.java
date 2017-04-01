package com.github.money.keeper.util.structure;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

public class TrieMapTest {

    @Test
    public void testPutGet() throws Exception {
        TrieMap<Integer> map = new TrieMap<>();
        map.put("/a/", 1);
        map.put("/b/bcd/", 2);
        map.put("/a/bc/ef/", 3);
        map.put("/a/bcd/e/", 4);
        map.put("/a/b/", 5);

        assertThat(map.getByFirstPrefix("/a/"), is(Pair.of("/a/", 1)));
        assertThat(map.getByFirstPrefix("/a/b/"), is(Pair.of("/a/", 1)));
        assertThat(map.getByFirstPrefix("/b/bcd/krt/"), is(Pair.of("/b/bcd/", 2)));
        assertThat(map.getByFirstPrefix(""), nullValue());
        assertThat(map.getByFirstPrefix("/b/"), nullValue());
    }
}