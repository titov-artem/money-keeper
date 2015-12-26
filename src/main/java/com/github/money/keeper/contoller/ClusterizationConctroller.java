package com.github.money.keeper.contoller;

import com.github.money.keeper.clusterization.StoreClusterizer;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class ClusterizationConctroller {

    private StoreClusterizer storeClusterizer;

    public ClusterizationResult clusterize(final List<Store> source, final List<SalePoint> input) {
        List<Store> stores = storeClusterizer.clusterize(source, input);
        Map<SalePoint, Store> spToStore = stores.stream()
                .flatMap(s -> s.getSalePoints().stream().map(p -> Pair.of(p, s)))
                .collect(toMap(Pair::getLeft, Pair::getRight));
        return new ClusterizationResult(stores, spToStore);
    }

    public static final class ClusterizationResult {
        private final ImmutableList<Store> stores;
        private final ImmutableMap<SalePoint, Store> salePointsStores;

        public ClusterizationResult(List<Store> stores, Map<SalePoint, Store> salePointsStores) {
            this.stores = ImmutableList.copyOf(stores);
            this.salePointsStores = ImmutableMap.copyOf(salePointsStores);
        }

        public ImmutableList<Store> getStores() {
            return stores;
        }

        public Store getStore(SalePoint salePoint) {
            return salePointsStores.get(salePoint);
        }
    }

    public void setStoreClusterizer(StoreClusterizer storeClusterizer) {
        this.storeClusterizer = storeClusterizer;
    }
}
