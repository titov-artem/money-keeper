package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.storage.BaseRepo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toMap;
import static org.slf4j.LoggerFactory.getLogger;

@NotThreadSafe
public abstract class AbstractInMemoryFileBackedRepo<K, V> extends AbstractFileBackedRepo implements BaseRepo<K, V> {

    private String storageFileName;
    private final ConcurrentMap<K, V> data = new ConcurrentHashMap<>();

    protected final ConcurrentMap<K, V> getData() {
        return data;
    }

    @PostConstruct
    public void init() throws IOException {
        setUpObjectMapper();
        if (storageFileName != null) {
            data.putAll(loadDataFromFile(storageFileName));
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (storageFileName != null) {
            storeDataToFile(data.values(), storageFileName);
        }
    }

    @Nonnull
    private Map<K, V> loadDataFromFile(String fileName) throws IOException {
        Map<K, V> out = Maps.newHashMap();
        File dataFile = new File(fileName);
        if (!dataFile.exists()) {
            return out;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.isBlank(line)) continue;
                line = line.trim();
                V value;
                try {
                    value = deserializeObject(objectMapper, line);
                } catch (Exception e) {
                    getLogger(getClass()).error("Failed to parse value from line: " + line);
                    continue;
                }
                K key = getKey(value);
                if (out.put(key, value) != null) {
                    getLogger(getClass()).warn("Duplicate value for key " + key);
                }
            }
        }
        return out;
    }

    private void storeDataToFile(Collection<V> data, String fileName) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            for (final V v : data) {
                out.write(String.format("%s%n", serializeObject(objectMapper, v)));
            }
        }
    }

    protected String serializeObject(ObjectMapper objectMapper, V v) throws JsonProcessingException {
        return this.objectMapper.writeValueAsString(v);
    }

    protected abstract V deserializeObject(ObjectMapper objectMapper, String source) throws IOException;

    protected abstract K getKey(V source);

    @Override
    public List<V> save(Iterable<V> values) {
        this.data.putAll(StreamSupport.stream(values.spliterator(), false)
                .collect(toMap(this::getKey, v -> v)));
        return Lists.newArrayList(values);
    }

    @Override
    public void delete(K key) {
        this.getData().remove(key);
    }

    @Override
    public List<V> loadAll() {
        return Lists.newArrayList(getData().values());
    }

    @Override
    public V load(K key) {
        return getData().get(key);
    }

    @Override
    public void clear() {
        data.clear();
    }

    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }
}
