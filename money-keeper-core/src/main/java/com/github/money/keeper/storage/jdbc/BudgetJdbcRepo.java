package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.Budget;
import com.github.money.keeper.storage.BudgetRepo;
import com.google.common.collect.Sets;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.SqlUtils.toDate;
import static com.github.money.keeper.storage.jdbc.generated.Tables.BUDGET;
import static java.util.stream.Collectors.toList;

@Repository
public class BudgetJdbcRepo extends AbstractJdbcRepo<Long, Budget> implements BudgetRepo {

    private static Function<Record, Budget> MAPPER =
            record -> new Budget(
                    record.get(BUDGET.ID),
                    Sets.newHashSet(record.get(BUDGET.ACCOUNT_IDS)),
                    Sets.newHashSet(record.get(BUDGET.CATEGORY_IDS)),
                    record.get(BUDGET.NAME),
                    record.get(BUDGET.FROM).toLocalDate(),
                    record.get(BUDGET.TO).toLocalDate(),
                    record.get(BUDGET.AMOUNT)
            );
    private Clock clock;


    @Inject
    public BudgetJdbcRepo(Clock clock, JdbcHelper jdbc, TxHelper txHelper) {
        super(
                MAPPER,
                (record, budget, withPrimary) -> {
                    if (withPrimary) {
                        record.set(BUDGET.ID, budget.getId());
                    }
                    record.set(BUDGET.ACCOUNT_IDS, budget.getAccountIds().toArray(new Long[budget.getAccountIds().size()]));
                    record.set(BUDGET.CATEGORY_IDS, budget.getCategoryIds().toArray(new Long[budget.getCategoryIds().size()]));
                    record.set(BUDGET.NAME, budget.getName());
                    record.set(BUDGET.FROM, toDate(budget.getFrom(), clock));
                    record.set(BUDGET.TO, toDate(budget.getTo(), clock));
                    record.set(BUDGET.AMOUNT, budget.getAmount());
                },
                BUDGET,
                BUDGET.ID,
                jdbc,
                txHelper
        );
        this.clock = clock;
    }

    @Override public List<Budget> getAffectedBudgets(LocalDate from, LocalDate to) {
        return jdbc.DSL().select().from(BUDGET)
                // budget inside interval
                .where(BUDGET.FROM.greaterOrEqual(toDate(from, clock)).and(BUDGET.TO.lessOrEqual(toDate(to, clock))))
                // budget on the right from interval with overlap
                .or(BUDGET.FROM.greaterOrEqual(toDate(from, clock)).and(BUDGET.FROM.lessOrEqual(toDate(to, clock))))
                // budget on the left from interval with overlap
                .or(BUDGET.TO.greaterOrEqual(toDate(from, clock)).and(BUDGET.TO.lessOrEqual(toDate(to, clock))))
                // interval inside budget
                .or(BUDGET.FROM.lessOrEqual(toDate(from, clock)).and(BUDGET.TO.greaterOrEqual(toDate(to, clock))))
                .fetch()
                .stream()
                .map(MAPPER)
                .collect(toList());
    }

}
