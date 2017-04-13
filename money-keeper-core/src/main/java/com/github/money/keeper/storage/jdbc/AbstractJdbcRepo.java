package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.AbstractEntity;
import com.github.money.keeper.storage.BaseRepo;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.jooq.*;

import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;

public abstract class AbstractJdbcRepo<K, V extends AbstractEntity<K>> implements BaseRepo<K, V> {

    protected final Function<Record, V> mapper;
    protected final RecordFiller<V> recordFiller;
    protected final Table<? extends UpdatableRecord> table;
    protected final Field<K> idField;

    protected final JdbcHelper jdbc;
    protected final TxHelper txHelper;

    public AbstractJdbcRepo(Function<Record, V> mapper,
                            RecordFiller<V> recordFiller,
                            Table<? extends UpdatableRecord> table,
                            Field<K> idField,
                            JdbcHelper jdbc,
                            TxHelper txHelper) {
        this.mapper = mapper;
        this.recordFiller = recordFiller;
        this.table = table;
        this.idField = idField;
        this.jdbc = jdbc;
        this.txHelper = txHelper;
    }

    @Override public V save(V value) {
        return save(Collections.singletonList(value)).get(0);
    }

    @Override public List<V> save(Iterable<V> values) {
        return txHelper.withTx(() -> {
            List<V> toCreate = new ArrayList<>();
            List<V> toUpdate = new ArrayList<>();
            for (V value : values) {
                if (AbstractEntity.isFakeId(value.getId())) {
                    toCreate.add(value);
                } else {
                    toUpdate.add(value);
                }
            }
            insert(toCreate);
            update(toUpdate);
            return Lists.newArrayList(values);
        });
    }
    /*
    inserting maybe do like this:

    InsertSetStep<Record> insertStep = jdbc.DSL().insertInto(TABLE);

        Result<Record> result = null;
        for (Iterator<Account> iterator = accounts.iterator(); iterator.hasNext(); ) {
            Account account = iterator.next();
            InsertSetMoreStep<Record> step = insertStep
                    .set(NAME, account.getName())
                    .set(PARSER_TYPE, account.getParserType().name());
            if (iterator.hasNext()) {
                insertStep = step.newRecord();
            } else {
                result = step.returning(ID).fetch();
            }
        }

        if (result == null) {
            // nothing to save, return empty list
            return Collections.emptyList();
        }

        Iterator<Record> resIter = result.iterator();
        Iterator<Account> accountsIter = accounts.iterator();
        for (; resIter.hasNext(); ) {
            Record record = resIter.next();
            Account account = accountsIter.next();
            account.injectId(record.get(ID));
        }
        return Lists.newArrayList(accounts);
     */

    private List<V> insert(Iterable<V> values) {
        List<V> out = new ArrayList<>();
        for (V value : values) {
            out.add(insert(value));
        }
        return out;
    }

    private V insert(V value) {
        InsertSetMoreStep<? extends UpdatableRecord> step = jdbc.DSL().insertInto(table).set(Collections.emptyMap());
        UpdatableRecord record = jdbc.DSL().newRecord(table);
        recordFiller.fill(record, value, false);
        for (Field field : record.fields()) {
            if (field.equals(idField)) continue;
            step = step.set(field, record.get(field));
        }

        UpdatableRecord result = step.returning(idField).fetchOne();
        value.injectId(result.get(idField));
        return value;
    }

    private List<V> update(List<V> values) {
        List<UpdatableRecord<?>> records = new ArrayList<>();
        for (V value : values) {
            UpdatableRecord record = jdbc.DSL().newRecord(table);
            recordFiller.fill(record, value, true);
            records.add(record);
        }
        jdbc.DSL().batchUpdate(records).execute();
        return values;
    }

    @Override public void delete(K key) {
        jdbc.DSL().deleteFrom(table).where(idField.eq(key))
                .execute();
    }

    @Override public void delete(Iterable<K> keys) {
        txHelper.withTx(() -> {
            for (List<K> chunk : Iterables.partition(keys, jdbc.getMaxInSize())) {
                jdbc.DSL().deleteFrom(table).where(idField.in(chunk))
                        .execute();
            }
        });
    }

    @Override public Optional<V> get(K key) {
        return jdbc.DSL().select().from(table).where(idField.eq(key))
                .fetchOptional()
                .map(mapper);
    }

    @Override public List<V> get(Collection<K> keys) {
        List<V> out = new ArrayList<>();
        for (List<K> chunk : Iterables.partition(keys, jdbc.getMaxInSize())) {
            jdbc.DSL().select().from(table).where(idField.in(chunk))
                    .fetch()
                    .stream()
                    .map(mapper)
                    .forEach(out::add);
        }
        return out;
    }

    @Override public List<V> getAll() {
        return jdbc.DSL().select().from(table).fetch().stream().map(mapper).collect(toList());
    }

    @Override public void clear() {
        jdbc.DSL().deleteFrom(table);
    }

    protected interface RecordFiller<T> {
        void fill(Record record, T item, boolean withPrimary);
    }
}
