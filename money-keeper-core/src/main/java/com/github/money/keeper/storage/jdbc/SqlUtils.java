package com.github.money.keeper.storage.jdbc;

import java.sql.Date;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;

public class SqlUtils {

    public static final Instant MIN = Instant.ofEpochMilli(0);
    public static final Instant MAX = Instant.ofEpochMilli(5617075288770000L);

    public static Date toDate(LocalDate date, Clock clock) {
        Instant instant = date
                .atStartOfDay()
                .atZone(clock.getZone())
                .toInstant();
        if (instant.isBefore(MIN)) {
            instant = MIN;
        }
        if (instant.isAfter(MAX)) {
            instant = MAX;
        }
        return new Date(Date.from(instant).getTime());
    }
}