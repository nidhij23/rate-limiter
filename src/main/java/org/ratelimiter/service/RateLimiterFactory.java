package org.ratelimiter.service;

import org.ratelimiter.interfaces.CounterStore;
import org.ratelimiter.interfaces.RateLimiter;
import org.ratelimiter.service.impl.LeakyBucketRateLimiter;

public class RateLimiterFactory {

    private final CounterStore store;

    public RateLimiterFactory(CounterStore store) {
        this.store = store;
    }

    public RateLimiter create(String algorithm, int limit, long windowSeconds) {

        return switch (algorithm.toLowerCase()) {

            case "fixed_window" ->
                    new FixedWindowRateLimiter(store, limit, windowSeconds * 1000L);

            case "sliding_log" ->
                    new SlidingWindowLogRateLimiter(store, limit, windowSeconds * 1000L);

            case "sliding_counter" ->
                    new SlidingWindowCounterRateLimiter(store, limit, windowSeconds * 1000L);

            case "token_bucket" ->
                    new TokenBucketRateLimiter(store, limit, limit /(int) windowSeconds);

            case "leaky_bucket" ->
                    new LeakyBucketRateLimiter(store, limit, limit /(int) windowSeconds);

            default -> throw new IllegalArgumentException("Unknown algorithm " + algorithm);
        };
    }
}

