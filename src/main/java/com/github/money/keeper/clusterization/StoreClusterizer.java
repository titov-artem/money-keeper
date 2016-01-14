package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.util.advanced.strings.StringUtils;
import com.github.money.keeper.util.math.LevenshteinDistance;
import com.google.common.collect.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

import static java.util.stream.Collectors.toMap;

/**
 * Group input sale points to existing stores, or create new ones
 */
public class StoreClusterizer {

    private SalePointClusterizer salePointClusterizer;

    public StoreClusterizer(SalePointClusterizer salePointClusterizer) {
        this.salePointClusterizer = salePointClusterizer;
    }

    public ClusterizationResult clusterize(final List<Store> source, final Collection<SalePoint> input) {
        List<SalePoint> all = getAllSalePoints(source, input);
        List<Store> clusterizedStores = clusterize(all);
        List<Store> out = mergeStores(source, clusterizedStores);

        return buildClusterizationResult(out);
//        return buildClusterizationResult(clusterizedStores);
    }

    private List<SalePoint> getAllSalePoints(List<Store> source, Collection<SalePoint> input) {
        List<SalePoint> all = Lists.newArrayList(input);
        source.stream().forEach(s -> all.addAll(s.getSalePoints()));
        return all;
    }

    private List<Store> clusterize(List<SalePoint> all) {
        List<Set<SalePoint>> clusters = salePointClusterizer.clusterize(all,
                (p1, p2) -> LevenshteinDistance.distance(p1.getName(), p2.getName()));
        List<Store> clusterizedStores = Lists.newArrayList();
        for (final Set<SalePoint> cluster : clusters) {
            clusterizedStores.add(buildStoreFromCluster(cluster));
        }
        return clusterizedStores;
    }

    private List<Store> mergeStores(List<Store> before, List<Store> after) {
        SetMultimap<Store, Store> beforeToAfter = getAtoBCorrespondence(before, after);
        SetMultimap<Store, Store> afterToBefore = getAtoBCorrespondence(after, before);

        Set<Store> out = Sets.newHashSet();
        List<StoreRelationsUpdate> updates = Lists.newArrayList();
        for (Map.Entry<Store, Collection<Store>> entry : beforeToAfter.asMap().entrySet()) {
            Store storeBefore = entry.getKey();
            Collection<Store> storesAfter = entry.getValue();
            // TODO: support manually created stores
            // Remember: if store 1 was manually created so we have two cases:
            // 1. It was extended during clusterization. So it is OK
            // 2. It was partitioned or united during clusterization. It is forbidden. So we need to extract it points
            //    from other stores and add this store manually to output


            if (storesAfter.size() == 1) {
                Store storeAfter = storesAfter.iterator().next();
                Set<Store> aftersStoreBefore = afterToBefore.get(storeAfter);

                // We have store 1. It has points, that are belong only to store 2
                // So store 2 must contains store 1 among stores from which it was built
                assert aftersStoreBefore.contains(storeBefore);

                if (aftersStoreBefore.size() == 1) {
                    // 1-to-1 matching. Permitted case
                    addToOutput(storeBefore, storeAfter, true, out, updates);
                } else {
                    // Store 1 was united with another store
                    if (storeBefore.isManuallyCreated()) {
                        // TODO: support manually created stores
                        // currently we just accpet it, but after we need to perform actions, described in case 2
                        throw new UnsupportedOperationException("Add support of manually created stores");
                    } else {
                        // All stores that was united will have update to this store 2
                        addToOutput(storeBefore, storeAfter, true, out, updates);
                    }
                }
            } else {
                // Store 1 was splitted on some stores
                if (storeBefore.isManuallyCreated()) {
                    // TODO: support manually created stores
                    // currently we just accpet it, but after we need to perform actions, described in case 2
                    throw new UnsupportedOperationException("Add support of manually created stores");
                } else {
                    // Store 1 was splitted into several stores after clusterization. We need to determine its successor.
                    // Algorithm will assume the store which contains most of store 1 points the successor of store 1
                    Store successor = getSuccessorStore(storeBefore, storesAfter);
                    assert successor != null;

                    // We add successor to output with update
                    addToOutput(storeBefore, successor, true, out, updates);

                    // No we need to add other stores to output without update.
                    // If they were built from any other store 1* then they will be again added to set, and no duplicates appears
                    // Also it is possible that some updates will be added and it's OK too. But if we will not add them now,
                    // it is possible to miss them at all
                    for (Store store : storesAfter) {
                        // we can add all storesAfter, because out is set and it's OK to add successor again :)
                        addToOutput(storeBefore, store, false, out, updates);
                    }

                }
            }
        }
        performStoreRelationUpdates(updates);

        Set<Store> newStores = Sets.difference(Sets.newHashSet(after), out);
        out.addAll(newStores);

        return Lists.newArrayList(out);
    }

    private void performStoreRelationUpdates(List<StoreRelationsUpdate> updates) {
        // TODO updates manual categories and manual sale point matching
    }

    private Store getSuccessorStore(Store storeBefore, Collection<Store> storesAfter) {
        int maxIntersectionSize = -1;
        Store successor = null;
        for (Store storeAfter : storesAfter) {
            int currentIntersectionSize = Sets.intersection(storeBefore.getSalePoints(), storeAfter.getSalePoints()).size();
            if (currentIntersectionSize > maxIntersectionSize) {
                maxIntersectionSize = currentIntersectionSize;
                successor = storeAfter;
            }
        }
        return successor;
    }

    private void addToOutput(Store storeBefore, Store storeAfter, boolean withUpdate, Set<Store> out, List<StoreRelationsUpdate> updates) {
        out.add(storeAfter);
        if (withUpdate && !storeAfter.getName().equals(storeBefore.getName())) {
            updates.add(new StoreRelationsUpdate(storeBefore.getName(), storeAfter.getName()));
        }
    }

    private SetMultimap<Store, Store> getAtoBCorrespondence(List<Store> a, List<Store> b) {
        Map<SalePoint, Store> pointToBStore = getSalePointStoreMap(b);
        SetMultimap<Store, Store> aToB = HashMultimap.create();
        for (Store storeA : a) {
            for (SalePoint point : storeA.getSalePoints()) {
                Store storeB = pointToBStore.get(point);
                if (storeB != null) {
                    aToB.put(storeA, storeB);
                }
            }
        }
        return aToB;
    }

    private Store buildStoreFromCluster(Set<SalePoint> cluster) {
        SortedMultiset<String> categoryDescriptions = TreeMultiset.create();
        Set<String> names = Sets.newHashSet();
        for (SalePoint point : cluster) {
            categoryDescriptions.add(point.getCategoryDescription());
            names.add(point.getName());
        }
        int maxCount = -1;
        SortedSet<String> categoryDescriptionsWithEqualCount = Sets.newTreeSet();
        for (Multiset.Entry<String> entry : categoryDescriptions.entrySet()) {
            if (entry.getCount() > maxCount) {
                maxCount = entry.getCount();
                categoryDescriptionsWithEqualCount.clear();
                categoryDescriptionsWithEqualCount.add(entry.getElement());
            } else if (entry.getCount() == maxCount) {
                categoryDescriptionsWithEqualCount.add(entry.getElement());
            }
        }
        String categoryDescription = categoryDescriptionsWithEqualCount.first();

        return new Store(StringUtils.greatestCommonSubstring(names), categoryDescription, cluster);
    }

    private ClusterizationResult buildClusterizationResult(List<Store> out) {
        Map<SalePoint, Store> spToStore = getSalePointStoreMap(out);
        return new ClusterizationResult(out, spToStore);
    }

    private Map<SalePoint, Store> getSalePointStoreMap(List<Store> out) {
        return out.stream()
                .flatMap(s -> s.getSalePoints().stream().map(p -> Pair.of(p, s)))
                .collect(toMap(Pair::getLeft, Pair::getRight));
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

    private static final class StoreRelationsUpdate {
        private final String storeOldName;
        private final String storeNewName;

        private StoreRelationsUpdate(String storeOldName, String storeNewName) {
            this.storeOldName = storeOldName;
            this.storeNewName = storeNewName;
        }

        public String getStoreOldName() {
            return storeOldName;
        }

        public String getStoreNewName() {
            return storeNewName;
        }
    }
}
