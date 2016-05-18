package com.github.money.keeper.storage.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.money.keeper.model.Account;
import com.github.money.keeper.parser.ParserType;
import com.github.money.keeper.storage.AccountRepo;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

public class InMemoryFileBackedAccountRepo extends AbstractInMemoryFileBackedRepo<Integer, Account> implements AccountRepo {

    private final AtomicInteger idGenerator = new AtomicInteger();

    @Override
    protected Account deserializeObject(ObjectMapper objectMapper, String source) throws IOException {
        Map<String, Object> data = objectMapper.readValue(source, Map.class);
        Account account = new Account(((Number) data.get("id")).intValue(), (String) data.get("name"), ParserType.valueOf((String) data.get("parserType")));
        if (account.getId() > idGenerator.get()) {
            idGenerator.set(account.getId());
        }
        return account;
    }

    @Override
    protected Integer getKey(Account source) {
        return source.getId();
    }

    @Override
    public List<Account> save(Iterable<Account> values) {
        return super.save(StreamSupport.stream(values.spliterator(), false)
                .map(t -> t.withId(idGenerator.incrementAndGet()))
                .collect(toList()));
    }
}