package com.github.money.keeper.app;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.time.Clock;

@Configuration
@ImportResource({"classpath*:/context/application-ctx.xml"})
@ComponentScan(basePackages = "com.github.money.keeper")
@PropertySource({
        "classpath:context/application.properties"
})
public class AppConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer properties() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
