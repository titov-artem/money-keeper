package com.github.money.keeper.clusterization;

import com.github.money.keeper.model.core.SalePoint;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class StorePrototype {

    private String name;
    private String category;
    private Set<SalePoint> salePoints;

    public StorePrototype(String name, String category, Set<SalePoint> salePoints) {
        this.name = name;
        this.category = category;
        this.salePoints = salePoints;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Set<SalePoint> getSalePoints() {
        return Collections.unmodifiableSet(salePoints);
    }

    public void removeAll(Collection<SalePoint> salePoints) {
        this.salePoints.removeAll(salePoints);
    }
}
