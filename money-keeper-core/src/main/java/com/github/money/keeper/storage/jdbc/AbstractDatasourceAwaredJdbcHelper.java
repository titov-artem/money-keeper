package com.github.money.keeper.storage.jdbc;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import javax.sql.DataSource;

public abstract class AbstractDatasourceAwaredJdbcHelper implements JdbcHelper {

    private final DataSource dataSource;

    protected AbstractDatasourceAwaredJdbcHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override public DSLContext DSL() {
        return DSL.using(dataSource, getDialect());
    }

    protected abstract SQLDialect getDialect();
}
