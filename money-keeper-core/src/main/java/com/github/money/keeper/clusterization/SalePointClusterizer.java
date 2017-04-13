package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.core.SalePoint;

import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

public interface SalePointClusterizer {

    List<Set<SalePoint>> clusterize(List<SalePoint> source,
                                    BiFunction<SalePoint, SalePoint, Integer> distance);
}
