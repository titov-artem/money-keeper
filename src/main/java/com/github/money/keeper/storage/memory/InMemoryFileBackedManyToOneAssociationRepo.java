package com.github.money.keeper.storage.memory;

import com.github.money.keeper.storage.ManyToOneAssociationRepo;
import com.google.common.collect.*;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

@NotThreadSafe
public class InMemoryFileBackedManyToOneAssociationRepo extends AbstractFileBackedRepo implements ManyToOneAssociationRepo {

    private static final String DEFAULT_FIRST_KEY_NAME = "key1";
    private static final String DEFAULT_SECOND_KEY_NAME = "key2";
    private String storageFileName;
    private final Map<String, String> manyToOneMapping = Maps.newHashMap();
    private final Multimap<String, String> oneToManyMapping = HashMultimap.create();

    private String firstKeyName = DEFAULT_FIRST_KEY_NAME;
    private String secondKeyName = DEFAULT_SECOND_KEY_NAME;

    @PostConstruct
    public void init() throws IOException {
        setUpObjectMapper();
        if (storageFileName != null) {
            loadDataFromFile(storageFileName).forEach((k, v) -> {
                manyToOneMapping.put(k, v);
                oneToManyMapping.put(v, k);
            });
        }
    }

    @PreDestroy
    public void destroy() throws IOException {
        if (storageFileName != null) {
            storeDataToFile(manyToOneMapping, storageFileName);
        }
    }

    @Nonnull
    private Map<String, String> loadDataFromFile(String fileName) throws IOException {
        BiMap<String, String> out = HashBiMap.create();
        File dataFile = new File(fileName);
        if (!dataFile.exists()) {
            return out;
        }
        try (BufferedReader in = new BufferedReader(new FileReader(dataFile))) {
            String line;
            while ((line = in.readLine()) != null) {
                if (StringUtils.isBlank(line)) continue;
                line = line.trim();
                Map<String, String> value;
                try {
                    value = objectMapper.readValue(line, Map.class);
                } catch (Exception e) {
                    getLogger(getClass()).error("Failed to parse value from line: " + line);
                    continue;
                }
                String key1 = value.get(firstKeyName);
                String key2 = value.get(secondKeyName);
                if (out.put(key1, key2) != null) {
                    getLogger(getClass()).warn("Duplicate value for key " + key1);
                }
            }
        }
        return out;
    }

    private void storeDataToFile(Map<String, String> data, String fileName) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fileName))) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                ImmutableMap<String, String> item = ImmutableMap.of(
                        firstKeyName, entry.getKey(),
                        secondKeyName, entry.getValue()
                );
                out.write(String.format("%s%n", objectMapper.writeValueAsString(item)));
            }
        }
    }

    @Override
    public void associate(String firstKey, String secondKey) {
        manyToOneMapping.put(firstKey, secondKey);
        oneToManyMapping.put(secondKey, firstKey);
    }

    @Override
    public void deleteByFirstKey(String key) {
        String secondKey = manyToOneMapping.remove(key);
        if (secondKey == null) return;
        oneToManyMapping.remove(secondKey, key);
    }

    @Override
    public void deleteBySecondKey(String key) {
        Collection<String> firstKeys = oneToManyMapping.get(key);
        for (String firstKey : firstKeys) {
            manyToOneMapping.remove(firstKey);
        }
        oneToManyMapping.removeAll(key);
    }

    @Override
    public String getByFirstKey(String key) {
        return manyToOneMapping.get(key);
    }

    @Override
    public List<String> getBySecondKey(String key) {
        return Lists.newArrayList(oneToManyMapping.get(key));
    }

    @Override
    public Map<String, String> getAllByFirstKey() {
        return Maps.newHashMap(manyToOneMapping);
    }

    @Override
    public Multimap<String, String> getAllBySecondKey() {
        return HashMultimap.create(oneToManyMapping);
    }

    @Override
    public void clear() {
        manyToOneMapping.clear();
        oneToManyMapping.clear();
    }

    public void setStorageFileName(String storageFileName) {
        this.storageFileName = storageFileName;
    }

    public void setFirstKeyName(String firstKeyName) {
        this.firstKeyName = firstKeyName;
    }

    public void setSecondKeyName(String secondKeyName) {
        this.secondKeyName = secondKeyName;
    }
}
