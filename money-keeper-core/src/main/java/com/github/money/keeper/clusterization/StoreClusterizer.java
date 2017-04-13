package com.github.money.keeper.clusterization;

import com.github.money.keeper.clusterization.merger.StoresMerger;
import com.github.money.keeper.clusterization.merger.action.ActionsCollector;
import com.github.money.keeper.clusterization.merger.action.StoreMergeAction;
import com.github.money.keeper.model.core.SalePoint;
import com.github.money.keeper.service.CategoryService;
import com.github.money.keeper.service.StoreService;
import com.github.money.keeper.util.advanced.strings.StringUtils;
import com.github.money.keeper.util.math.LevenshteinDistance;
import com.google.common.collect.Lists;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

/**
 * Group input sale points to existing stores, or create new ones.
 * <p>
 * Main idea is "take all sail points that exists in the system,
 * regroup into stores with some "magic" called "SalePointClusterizer"
 * and then match new clusters to earlier existed stores.
 * <p>
 * How to do it:
 * <p>
 * 1. For each sale point determine it's old store
 * 2. For each sale point determine new store, that will be built from cluster
 * 3. If new store name exactly equals to old store name we assume that it is the same store
 * 4. Otherwise we need to do this:
 * At first task all sale points, that belong to this store and determine new stores to
 * which they are belong. Find store, that has much of them. It will be successor of old store.
 * Other stores will be new store, that have to be created
 * <p>
 * If two stores have same successor, so we need to unite them
 */
@Service
public class StoreClusterizer {

    private final SalePointClusterizer salePointClusterizer;
    private final StoresMerger storesMerger;
    private final StoreService storeService;

    public StoreClusterizer(SalePointClusterizer salePointClusterizer,
                            StoresMerger storesMerger,
                            StoreService storeService) {
        this.salePointClusterizer = salePointClusterizer;
        this.storesMerger = storesMerger;
        this.storeService = storeService;
    }

    public void clusterize(final List<ClusterizableStore> source,
                           final Collection<SalePoint> input) {
        List<SalePoint> all = getAllSalePoints(source, input);
        List<StorePrototype> clusterizedStores = clusterize(all);
        List<StoreMergeAction> actions = storesMerger.merge(source, clusterizedStores);

        ActionsCollector collector = new ActionsCollector();
        actions.forEach(collector::collect);
        storeService.applyStoreMergeActions(collector);
    }

    private List<SalePoint> getAllSalePoints(List<ClusterizableStore> source, Collection<SalePoint> input) {
        List<SalePoint> all = Lists.newArrayList(input);
        source.forEach(s -> all.addAll(s.getSalePoints()));
        return all;
    }

    private List<StorePrototype> clusterize(List<SalePoint> all) {
        List<Set<SalePoint>> clusters = salePointClusterizer.clusterize(all,
                (p1, p2) -> LevenshteinDistance.distance(p1.getName(), p2.getName()));
        List<StorePrototype> clusterizedStores = Lists.newArrayList();
        for (final Set<SalePoint> cluster : clusters) {
            clusterizedStores.add(buildPrototypeFromCluster(cluster));
        }
        return clusterizedStores;
    }

    private StorePrototype buildPrototypeFromCluster(Set<SalePoint> cluster) {
        Set<String> names = cluster.stream()
                .map(SalePoint::getName)
                .collect(toSet());

        String rawCategory = cluster.stream()
                // group by category
                .collect(groupingBy(SalePoint::getRawCategory))
                .entrySet()
                .stream()
                // count sale points in each category
                .map(entry -> Tuple.tuple(entry.getValue().size(), entry.getKey()))
                // select first in lexicographic order with max sale points count
                .max(Comparator.naturalOrder())
                .map(Tuple2::v2)
                .orElse(CategoryService.UNKNOWN_CATEGORY_NAME);

        return new StorePrototype(
                StringUtils.greatestCommonSubstring(names),
                rawCategory,
                cluster);
    }
}
