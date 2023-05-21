package com.tasks.store.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
class GlobalConfig {

    private static final String TIME_ZONE = "Europe/Tallinn";

    @PostConstruct
    void setTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone(TIME_ZONE));
    }

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of(TIME_ZONE));
    }

}

