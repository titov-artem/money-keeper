package com.github.money.keeper.storage.jdbc;

import com.github.money.keeper.model.core.Account;
import com.github.money.keeper.parser.ParserType;
import com.github.money.keeper.storage.AccountRepo;
import org.jooq.Record;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.function.Function;

import static com.github.money.keeper.storage.jdbc.generated.Tables.ACCOUNT;

@Repository
public class AccountJdbcRepo extends AbstractJdbcRepo<Long, Account> implements AccountRepo {

    private static Function<Record, Account> MAPPER =
            record -> new Account(
                    record.get(ACCOUNT.ID),
                    record.get(ACCOUNT.NAME),
                    ParserType.valueOf(record.get(ACCOUNT.PARSER_TYPE))
            );


    @Inject
    public AccountJdbcRepo(JdbcHelper jdbc, TxHelper txHelper) {
        super(
                MAPPER,
                (record, account, withPrimary) -> {
                    if (withPrimary) {
                        record.set(ACCOUNT.ID, account.getId());
                    }
                    record.set(ACCOUNT.NAME, account.getName());
                    record.set(ACCOUNT.PARSER_TYPE, account.getParserType().name());
                },
                ACCOUNT,
                ACCOUNT.ID,
                jdbc,
                txHelper
        );
    }
}
