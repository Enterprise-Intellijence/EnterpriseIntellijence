package com.enterpriseintellijence.enterpriseintellijence.security.rateLimiter;

import com.enterpriseintellijence.enterpriseintellijence.security.Constants;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Refill;

import java.time.Duration;

public enum UserTypePlan {
    BASIC_USER (Bandwidth.classic(Constants.BASIC_USER_RATE_LIMIT_BANDWIDTH, Refill.intervally(Constants.BASIC_USER_RATE_LIMIT_REFILL, Duration.ofMinutes(Constants.BASIC_USER_RATE_LIMIT_REFILL_DURATION)))),
    ADMIN (Bandwidth.classic(Constants.ADMIN_RATE_LIMIT_BANDWIDTH, Refill.intervally(Constants.ADMIN_RATE_LIMIT_REFILL, Duration.ofSeconds(Constants.ADMIN_RATE_LIMIT_REFILL_DURATION))));

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
