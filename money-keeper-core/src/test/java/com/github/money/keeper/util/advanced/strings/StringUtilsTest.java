package com.github.money.keeper.util.advanced.strings;

import com.google.common.collect.Lists;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StringUtilsTest {

    @Test
    public void testGreatestCommonSubstring() throws Exception {
        assertThat(gcs(), is(""));
        assertThat(gcs("abc"), is("abc"));
        assertThat(gcs("abc", "def"), is(""));
        assertThat(gcs("abc", "aef"), is("a"));
        assertThat(gcs("cef", "aef"), is("ef"));
        assertThat(gcs("ABABC", "BABCA", "ABCBA"), is("ABC"));
    }

    private String gcs(String... source) {
        return StringUtils.greatestCommonSubstring(Lists.newArrayList(source));
    }
}