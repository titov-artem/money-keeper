package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

public class InMemoryFileBackedCategoryRepo extends AbstractInMemoryFileBackedRepo<String, Category> implements CategoryRepo {

    @Override
    protected Category deserializeObject(ObjectMapper objectMapper, String source) throws IOException {
        Map<String, Object> data = objectMapper.readValue(source, Map.class);
        return new Category((String) data.get("name"), ImmutableSet.<String>copyOf((Collection) data.get("alternatives")));
    }

    @Override
    protected String getKey(Category source) {
        return source.getName();
    }

}
