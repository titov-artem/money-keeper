package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.RawTransaction;
import com.github.money.keeper.storage.TransactionRepo;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class InMemoryFileBackedTransactionRepo extends AbstractInMemoryFileBackedRepo<Long, RawTransaction> implements TransactionRepo {

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
        return value;
    }

    @Override
    protected Long getKey(RawTransaction source) {
        return source.getId();
    }

    @Override
    public List<RawTransaction> save(Iterable<RawTransaction> values) {
        return super.save(StreamSupport.stream(values.spliterator(), false)
                .map(v -> v.withId(idGenerator.incrementAndGet()))
                .collect(Collectors.toList()));
    }

    @Override
    public List<RawTransaction> load(Instant from, Instant to) {
        // todo implement me when needed
        return null;
    }
}
