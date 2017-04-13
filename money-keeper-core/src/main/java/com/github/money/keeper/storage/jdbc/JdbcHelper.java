package com.github.money.keeper.storage.jdbc;

import org.jooq.DSLContext;

public interface JdbcHelper {

    DSLContext DSL();

    int getMaxInSize();

}
