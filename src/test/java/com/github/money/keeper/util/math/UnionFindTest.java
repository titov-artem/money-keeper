package com.github.money.keeper.util.math;

import com.github.money.keeper.util.structure.UnionFind;
import com.google.common.collect.Lists;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class UnionFindTest {

    @Test
    public void testUnionFind1() throws Exception {
        UnionFind<Integer> uf = new UnionFind<>(Lists.newArrayList(1, 2, 3, 4, 5));
        uf.union(1, 2);
        uf.union(3, 4);
        uf.union(4, 5);
        uf.union(3, 2);

        assertThat(uf.rootFor(1), is(uf.rootFor(5)));
    }

    @Test
    public void testUnionFind2() throws Exception {
        UnionFind<Integer> uf = new UnionFind<>(Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        uf.union(1, 2);
        uf.union(3, 4);
        uf.union(4, 5);
        uf.union(3, 2);
        uf.union(10, 11);
        uf.union(9, 12);
        uf.union(9, 10);
        uf.union(6, 7);
        uf.union(7, 8);
        uf.union(8, 9);

        assertThat(uf.rootFor(6), is(uf.rootFor(12)));
        assertThat(uf.rootFor(1).equals(uf.rootFor(12)), is(false));

        uf.union(5, 12);
        assertThat(uf.rootFor(1), is(uf.rootFor(12)));
    }
}