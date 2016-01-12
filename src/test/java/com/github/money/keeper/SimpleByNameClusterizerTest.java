package com.github.money.keeper;

import com.github.money.keeper.clusterization.SimpleByNameClusterizer;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.util.math.LevenshteinDistance;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SimpleByNameClusterizerTest {

    private SimpleByNameClusterizer clusterizer = new SimpleByNameClusterizer();

    @Test
    public void testClusterize() throws Exception {
        List<Set<SalePoint>> clusters = clusterizer.clusterize(list(point("abcd"), point("abef"), point("gfcd")), (p1, p2) -> LevenshteinDistance.distance(p1.getName(), p2.getName()));

        assertThat(clusters.size(), is(1));
        assertThat(clusters.get(0).stream().map(SalePoint::getName).collect(toSet()), is(Sets.newHashSet("abcd", "abef", "gfcd")));
    }

    private <T> List<T> list(T... a) {
        return Lists.newArrayList(a);
    }

    private SalePoint point(String name) {
        return new SalePoint(name, "");
    }

}