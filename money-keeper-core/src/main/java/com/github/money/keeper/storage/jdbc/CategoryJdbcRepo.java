package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.Category;
import com.github.money.keeper.storage.CategoryRepo;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.generated.Tables.CATEGORY;

@Repository
public class CategoryJdbcRepo extends AbstractJdbcRepo<Long, Category> implements CategoryRepo {

    private static Function<Record, Category> MAPPER =
            record -> new Category(
                    record.get(CATEGORY.ID),
                    record.get(CATEGORY.NAME),
                    Sets.newHashSet(record.get(CATEGORY.ALTERNATIVES))
            );

    @Inject
    public CategoryJdbcRepo(JdbcHelper jdbc, TxHelper txHelper) {
        super(
                MAPPER,
                (record, category, withPrimary) -> {
                    if (withPrimary) {
                        record.set(CATEGORY.ID, category.getId());
                    }
                    record.set(CATEGORY.NAME, category.getName());
                    record.set(CATEGORY.ALTERNATIVES, category.getAlternatives().toArray(new String[0]));
                },
                CATEGORY,
                CATEGORY.ID,
                jdbc,
                txHelper
        );
    }

    @Override
    public Optional<Category> findByName(String name) {
        return jdbc.DSL().select().from(CATEGORY).where(CATEGORY.NAME.eq(name))
                .fetchOptional()
                .map(MAPPER);
    }

    @Override
    public Map<String, Category> findByNames(Iterable<String> names) {
        Map<String, Category> out = new HashMap<>();
        for (List<String> chunk : Iterables.partition(names, jdbc.getMaxInSize())) {
            jdbc.DSL().select().from(CATEGORY).where(CATEGORY.NAME.in(chunk))
                    .fetch()
                    .stream()
                    .map(MAPPER)
                    .forEach(category -> out.put(category.getName(), category));
        }
        return out;
    }
}
