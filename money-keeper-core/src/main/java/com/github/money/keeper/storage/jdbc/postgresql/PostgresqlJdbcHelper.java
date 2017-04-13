package com.github.money.keeper.storage.jdbc.postgresql;

import com.github.money.keeper.storage.jdbc.AbstractDatasourceAwaredJdbcHelper;
import org.jooq.SQLDialect;

import javax.sql.DataSource;

public class PostgresqlJdbcHelper extends AbstractDatasourceAwaredJdbcHelper {

    public PostgresqlJdbcHelper(DataSource dataSource) {
        super(dataSource);
    }

    @Override protected SQLDialect getDialect() {
        return SQLDialect.POSTGRES_9_5;
    }

    @Override public int getMaxInSize() {
        return 30000;
    }
}
