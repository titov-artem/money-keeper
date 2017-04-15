package com.github.money.keeper.storage;

import com.github.money.keeper.model.core.SalePoint;

public interface SalePointRepo extends BaseRepo<Long, SalePoint> {

    void setStoreId(Iterable<Long> salePointIds, Long storeId);

}
