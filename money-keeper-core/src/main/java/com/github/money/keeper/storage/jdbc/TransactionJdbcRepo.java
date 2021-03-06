package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.RawTransaction;
import com.github.money.keeper.storage.TransactionRepo;
import org.jooq.Condition;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import javax.inject.Inject;
import java.sql.Date;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.SqlUtils.toDate;
import static com.github.money.keeper.storage.jdbc.generated.Tables.TRANSACTION;
import static java.util.stream.Collectors.toList;

@Repository
public class TransactionJdbcRepo extends AbstractJdbcRepo<Long, RawTransaction> implements TransactionRepo {

    private static Function<Record, RawTransaction> MAPPER =
            record -> new RawTransaction(
                    record.get(TRANSACTION.ID),
                    record.get(TRANSACTION.ACCOUNT_ID),
                    record.get(TRANSACTION.DATE).toLocalDate(),
                    record.get(TRANSACTION.SALE_POINT_ID),
                    record.get(TRANSACTION.AMOUNT),
                    record.get(TRANSACTION.FILE_HASH),
                    record.get(TRANSACTION.UPLOAD_ID)
            );

    private final Clock clock;

    @Inject
    public TransactionJdbcRepo(Clock clock, JdbcHelper jdbc, TxHelper txHelper) {
        super(
                MAPPER,
                (record, transaction, withPrimary) -> {
                    if (withPrimary) {
                        record.set(TRANSACTION.ID, transaction.getId());
                    }
                    record.set(TRANSACTION.ACCOUNT_ID, transaction.getAccountId());
                    Date date = toDate(transaction.getDate(), clock);
                    record.set(TRANSACTION.DATE, date);
                    record.set(TRANSACTION.SALE_POINT_ID, transaction.getSalePointId());
                    record.set(TRANSACTION.AMOUNT, transaction.getAmount());
                    record.set(TRANSACTION.FILE_HASH, transaction.getFileHash());
                    record.set(TRANSACTION.UPLOAD_ID, transaction.getUploadId());
                },
                TRANSACTION,
                TRANSACTION.ID,
                jdbc,
                txHelper
        );
        this.clock = clock;
    }

    @Override
    public List<RawTransaction> load(@Nullable LocalDate from,
                                     @Nullable LocalDate to) {
        List<Condition> conditions = new ArrayList<>();
        if (from != null) {
            conditions.add(TRANSACTION.DATE.greaterOrEqual(toDate(from, clock)));
        }
        if (to != null) {
            conditions.add(TRANSACTION.DATE.lessOrEqual(toDate(to, clock)));
        }
        return jdbc.DSL().select().from(table)
                .where(conditions)
                .fetch()
                .stream()
                .map(MAPPER)
                .collect(toList());
    }

    @Override
    public List<RawTransaction> load(@Nullable LocalDate from,
                                     @Nullable LocalDate to,
                                     @Nullable Set<Long> accountIds) {
        List<Condition> conditions = new ArrayList<>();
        if (from != null) {
            conditions.add(TRANSACTION.DATE.greaterOrEqual(toDate(from, clock)));
        }
        if (to != null) {
            conditions.add(TRANSACTION.DATE.lessOrEqual(toDate(to, clock)));
        }
        if (accountIds != null && !accountIds.isEmpty()) {
            conditions.add(TRANSACTION.ACCOUNT_ID.in(accountIds));
        }
        return jdbc.DSL().select().from(table)
                .where(conditions)
                .fetch()
                .stream()
                .map(MAPPER)
                .collect(toList());
    }

}
