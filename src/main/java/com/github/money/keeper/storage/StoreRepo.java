package com.github.money.keeper.storage;

import com.github.money.keeper.model.Store;

import java.util.List;

public interface StoreRepo {

    void save(Iterable<Store> stores);

    List<Store> loadAll();

}
