package com.enterpriseintellijence.enterpriseintellijence.security.rateLimiter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;

public enum UserTypePlan {
    BASIC_USER (Bandwidth.classic(15, Refill.intervally(10, Duration.ofMinutes(2)))),
    ADMIN (Bandwidth.classic(100, Refill.intervally(100, Duration.ofSeconds(1))));

    Bandwidth limit;

    Bandwidth getLimit() {
        return limit;
    }

    UserTypePlan(Bandwidth limit) {
        this.limit=limit;
    }


    static UserTypePlan resolvePlanFromKey(String key) {
        if (key.startsWith("BASIC-")) {
            return BASIC_USER;
        } else if (key.startsWith("ADMIN-")) {
            return ADMIN;
        }
        return BASIC_USER;
    }
}
