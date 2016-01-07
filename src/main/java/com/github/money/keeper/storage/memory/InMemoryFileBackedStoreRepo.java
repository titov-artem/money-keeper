package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.SalePoint;
import com.github.money.keeper.model.Store;
import com.github.money.keeper.storage.StoreRepo;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toSet;

public class InMemoryFileBackedStoreRepo extends AbstractInMemoryFileBackedRepo<String, Store> implements StoreRepo {
    @Override
    protected Store deserializeObject(ObjectMapper objectMapper, String source) throws IOException {
        Map map = objectMapper.readValue(source, Map.class);
        return new Store((String) map.get("name"), (String) map.get("categoryDescription"),
                ((Collection<Map>) map.get("salePoints")).stream().map(this::deserializeSalePoint).collect(toSet())
        );
    }

    private SalePoint deserializeSalePoint(Map source) {
        return new SalePoint((String) source.get("name"), (String) source.get("categoryDescription"));
    }

    @Override
    protected String getKey(Store source) {
        return source.getName();
    }
}
