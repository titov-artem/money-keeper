package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;

public class InMemoryFileBackedCategoryRepo implements CategoryRepo {
    private static final Logger log = LoggerFactory.getLogger(InMemoryFileBackedCategoryRepo.class);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ConcurrentMap<String, Category> categories = new ConcurrentHashMap<>();

    private String storageFileName;

    @PostConstruct
    public void init() throws IOException {
        if (storageFileName != null) {
            categories.putAll(loadCategoriesFromFile(storageFileName));
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (storageFileName != null) {
            storeCategoriesToFile(categories.values(), storageFileName);
        }
    }

    @Nonnull
    private Map<String, Category> loadCategoriesFromFile(String fileName) throws IOException {
        Map<String, Category> out = Maps.newHashMap();
        try (BufferedReader in = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.isBlank(line)) continue;
                line = line.trim();
                Category category;
                try {
                    Map<String, Object> data = objectMapper.readValue(line, Map.class);
                    category = new Category((String) data.get("name"), ImmutableSet.copyOf((Collection) data.get("alternatives")));
                } catch (IOException e) {
                    log.error("Failed to parse category from line: " + line);
                    continue;
                }
                if (out.put(category.getName(), category) != null) {
                    log.warn("Duplicate categories for name " + category.getName());
                }
            }
        }
        return out;
    }

    private void storeCategoriesToFile(Collection<Category> categories, String fileName) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            for (final Category category : categories) {
                out.write(String.format("%s%n", objectMapper.writeValueAsString(category)));
            }
        }
    }


    @Override
    public void save(Iterable<Category> categories) {
        this.categories.putAll(StreamSupport.stream(categories.spliterator(), false)
                .collect(toMap(Category::getName, c -> c)));
    }

    @Override
    public void delete(String name) {
        this.categories.remove(name);
    }

    @Override
    public List<Category> loadAll() {
        return Lists.newArrayList(categories.values());
    }

    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }
}
