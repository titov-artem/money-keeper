package com.github.money.keeper.util.math;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LevenshteinDistanceTest {

    @Test
    public void testDistanceOneLetterWords() throws Exception {
        assertThat(LevenshteinDistance.distance("a", "a"), is(0));
        assertThat(LevenshteinDistance.distance("a", "b"), is(1));
        assertThat(LevenshteinDistance.distance("a", ""), is(1));
        assertThat(LevenshteinDistance.distance("", "b"), is(1));
    }

    @Test
    public void testDistance() throws Exception {
        assertThat(LevenshteinDistance.distance("abc", "abc"), is(0));
        assertThat(LevenshteinDistance.distance("abc", "ac"), is(1));
        assertThat(LevenshteinDistance.distance("acb", "abc"), is(2));
        assertThat(LevenshteinDistance.distance("арестант", "дагестан"), is(3));
    }

    @Test
    public void testDistance2() throws Exception {
        assertThat(LevenshteinDistance.distance("abcd", "gfcd"), is(2));
    }
}