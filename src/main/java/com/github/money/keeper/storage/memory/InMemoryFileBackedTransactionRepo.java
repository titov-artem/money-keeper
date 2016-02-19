package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.storage.TransactionRepo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@NotThreadSafe
public class InMemoryFileBackedTransactionRepo extends AbstractInMemoryFileBackedRepo<Long, RawTransaction> implements TransactionRepo {

    private final SortedMap<LocalDate, List<RawTransaction>> dateIndex = Maps.newTreeMap();
    private final AtomicLong idGenerator = new AtomicLong();

    @Override
    protected String serializeObject(ObjectMapper objectMapper, RawTransaction rawTransaction) throws JsonProcessingException {
        return super.serializeObject(objectMapper, rawTransaction);
    }

    @Override
    protected RawTransaction deserializeObject(ObjectMapper objectMapper, String source) throws IOException {
        RawTransaction value = objectMapper.readValue(source, RawTransaction.class);
        if (value.getId() > idGenerator.get()) {
            idGenerator.set(value.getId());
        }
        getBucket(value.getDate()).add(value);
        return value;
    }

    private List<RawTransaction> getBucket(LocalDate date) {
        List<RawTransaction> list = dateIndex.get(date);
        if (list == null) {
            list = Lists.newArrayList();
            dateIndex.put(date, list);
        }
        return list;
    }

    @Override
    protected Long getKey(RawTransaction source) {
        return source.getId();
    }

    @Override
    public List<RawTransaction> save(Iterable<RawTransaction> values) {
        return super.save(StreamSupport.stream(values.spliterator(), false)
                .map(t -> t.withId(idGenerator.incrementAndGet()))
                .peek(t -> getBucket(t.getDate()).add(t))
                .collect(toList()));
    }

    @Override
    public void delete(Long key) {
        RawTransaction transaction = getData().get(key);
        if (transaction != null) {
            getBucket(transaction.getDate()).remove(transaction);
        }
        super.delete(key);
    }

    @Override
    public void clear() {
        dateIndex.clear();
        super.clear();
    }

    @Override
    public List<RawTransaction> load(@Nullable LocalDate from, @Nullable LocalDate to) {
        if (dateIndex.isEmpty()) return Collections.emptyList();

        LocalDate left = from == null ? dateIndex.firstKey() : from;
        LocalDate right = to != LocalDate.MAX
                ? (to == null ? dateIndex.lastKey() : to).plusDays(1)
                : to;
        if (left.isAfter(right)) return Collections.emptyList();
        return dateIndex.subMap(left, right).values().stream().flatMap(Collection::stream).collect(toList());
    }
}
