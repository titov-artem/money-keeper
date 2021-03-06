package com.github.money.keeper.util;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.PostConstruct;

/**
 * @author Artem Titov titov.artem.u@yandex.com
 */
public class FlywayInit {

    private Flyway flyway;

    @PostConstruct
    public void init() {
        flyway.clean();
        flyway.migrate();
    }

    @Required
    public void setFlyway(Flyway flyway) {
        this.flyway = flyway;
    }
}
