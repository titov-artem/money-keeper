package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.Store;
import com.github.money.keeper.storage.StoreRepo;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import org.jooq.Query;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.generated.Tables.STORE;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Repository
public class StoreJdbcRepo extends AbstractJdbcRepo<Long, Store> implements StoreRepo {

    private static Function<Record, Store> MAPPER =
            record -> new Store(
                    record.get(STORE.ID),
                    record.get(STORE.NAME),
                    record.get(STORE.CATEGORY_ID)
            );

    @Inject
    public StoreJdbcRepo(JdbcHelper jdbc,
                         TxHelper txHelper) {
        super(
                MAPPER,
                (record, store, withPrimary) -> {
                    if (withPrimary) {
                        record.set(STORE.ID, store.getId());
                    }
                    record.set(STORE.NAME, store.getName());
                    record.set(STORE.CATEGORY_ID, store.getCategoryId());
                },
                STORE,
                STORE.ID,
                jdbc,
                txHelper
        );
    }

    @Override
    public List<Store> findAllByCategory(Long categoryId) {
        return jdbc.DSL().select().from(STORE).where(STORE.CATEGORY_ID.eq(categoryId))
                .fetch()
                .stream()
                .map(MAPPER)
                .collect(toList());
    }

    @Override
    public Map<Long, List<Store>> getAllByCategories() {
        return jdbc.DSL().select().from(STORE)
                .fetch()
                .stream()
                .map(MAPPER)
                .collect(groupingBy(Store::getCategoryId));
    }

    @Override
    public void setCategoryId(Iterable<Long> storeIds, Long categoryId) {
        txHelper.withTx(() -> {
            for (List<Long> chunk : Iterables.partition(storeIds, jdbc.getMaxInSize())) {
                jdbc.DSL().update(STORE)
                        .set(STORE.CATEGORY_ID, categoryId)
                        .where(STORE.ID.in(chunk))
                        .execute();
            }
        });
    }

    @Override public void batchRename(List<Long> storeIds, List<String> storeNames) {
        Preconditions.checkArgument(storeIds.size() == storeNames.size());
        Query query = jdbc.DSL().update(STORE).set(STORE.NAME, (String) null).where(STORE.ID.eq((Long) null));
        Object[][] params = new Object[storeIds.size()][2];
        for (int i = 0; i < storeIds.size(); i++) {
            params[i][0] = storeIds.get(i);
            params[i][1] = storeNames.get(i);
        }
        jdbc.DSL().batch(query, params).execute();
    }
}
