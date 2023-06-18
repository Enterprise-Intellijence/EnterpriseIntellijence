package com.enterpriseintellijence.enterpriseintellijence.security.rateLimiter;

import com.enterpriseintellijence.enterpriseintellijence.data.entities.User;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserTypePlanService {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String apiKey) {
        return cache.computeIfAbsent(apiKey, this::newBucket);
    }

    private Bucket newBucket(String key) {
        UserTypePlan pricingPlan = UserTypePlan.resolvePlanFromKey(key);

        return Bucket.builder()
                .addLimit(pricingPlan.getLimit())
                .build();
    }
}
