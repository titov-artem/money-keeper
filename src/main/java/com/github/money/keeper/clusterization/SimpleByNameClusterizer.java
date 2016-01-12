package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.util.structure.UnionFind;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

import java.util.*;
import java.util.function.BiFunction;

public class SimpleByNameClusterizer implements SalePointClusterizer {

    @Override
    public List<Set<SalePoint>> clusterize(List<SalePoint> source, BiFunction<SalePoint, SalePoint, Integer> distance) {
        SetMultimap<Integer, UnorderedPair<SalePoint>> distanceToPair = HashMultimap.create();
        for (final SalePoint first : source) {
            for (final SalePoint second : source) {
                Integer d = distance.apply(first, second);
                distanceToPair.put(d, pair(first, second));
            }
        }

        UnionFind<SalePoint> uf = new UnionFind<>(source);
        for (final Map.Entry<Integer, UnorderedPair<SalePoint>> e : distanceToPair.entries()) {
            if (checkDistance(e.getValue().getFirst(), e.getValue().getSecond(), e.getKey())) {
                uf.union(e.getValue().getFirst(), e.getValue().getSecond());
            }
        }

        // cluster root to cluster
        SetMultimap<SalePoint, SalePoint> clusters = HashMultimap.create();
        for (final SalePoint item : source) {
            clusters.put(uf.rootFor(item), item);
        }
        List<Set<SalePoint>> out = Lists.newArrayListWithCapacity(clusters.asMap().size());
        for (final Map.Entry<SalePoint, Collection<SalePoint>> entry : clusters.asMap().entrySet()) {
            out.add(Sets.newHashSet(entry.getValue()));
        }
        return out;
    }

    /**
     * Check does specified distance small enough to put these objects in one cluster
     *
     * @param t1
     * @param t2
     * @param distance
     * @return
     */
    private boolean checkDistance(SalePoint t1, SalePoint t2, int distance) {
        return distance <= t1.getName().length() / 2 && distance <= t2.getName().length() / 2;
    }

    private UnorderedPair<SalePoint> pair(SalePoint t1, SalePoint t2) {
        return new UnorderedPair<>(t1, t2);
    }

    private static final class UnorderedPair<T> {
        private final T first;
        private final T second;

        private UnorderedPair(T first, T second) {
            boolean firstLess = first.hashCode() < second.hashCode();
            this.first = firstLess ? first : second;
            this.second = firstLess ? second : first;
        }

        public T getFirst() {
            return first;
        }

        public T getSecond() {
            return second;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnorderedPair<?> that = (UnorderedPair<?>) o;
            return Objects.equals(first, that.first) &&
                    Objects.equals(second, that.second);
        }

        @Override public int hashCode() {
            return Objects.hash(first, second);
        }
    }

}
