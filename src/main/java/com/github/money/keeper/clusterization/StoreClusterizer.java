package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.util.math.LevenshteinDistance;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

/**
 * Group input sale points to existing stores, or create new ones
 */
public class StoreClusterizer {

    private SalePointClusterizer salePointClusterizer;

    public StoreClusterizer(SalePointClusterizer salePointClusterizer) {
        this.salePointClusterizer = salePointClusterizer;
    }

    public List<Store> clusterize(final List<Store> source, final List<SalePoint> input) {
        List<SalePoint> all = Lists.newArrayList(input);
        source.stream().forEach(s -> all.addAll(s.getSalePoints()));
        List<Set<SalePoint>> clusters = salePointClusterizer.clusterize(all, LevenshteinDistance::distance);
        List<Store> out = Lists.newArrayList();
        for (final Set<SalePoint> cluster : clusters) {
            SalePoint head = cluster.iterator().next();
            out.add(new Store(head.getName(), head.getDescription(), cluster));
        }
        return out;
    }
}
