package org.ratelimiter.service.impl;

import org.ratelimiter.interfaces.RateLimiter;
import org.ratelimiter.interfaces.CounterStore;

public class LeakyBucketRateLimiter implements RateLimiter {

    private final CounterStore store;
    private final int capacity;
    private final int leakRatePerSec;

    public LeakyBucketRateLimiter(CounterStore store, int capacity, int leakRatePerSec) {
        this.store = store;
        this.capacity = capacity;
        this.leakRatePerSec = leakRatePerSec;
    }

    @Override
    public synchronized boolean allowRequest(String key, int hits) {

        String waterKey = key + ":water";
        String lastLeakKey = key + ":lastLeak";

        long now = System.currentTimeMillis();

        long water = store.get(waterKey);
        long lastLeak = store.get(lastLeakKey);

        if (lastLeak == 0) lastLeak = now;

        long seconds = (now - lastLeak) / 1000;
        long leaked = seconds * leakRatePerSec;

        water = Math.max(0, water - leaked);

        if (water + hits > capacity) {
            store.set(waterKey, water, Long.MAX_VALUE);
            store.set(lastLeakKey, now, Long.MAX_VALUE);
            return false;
        }

        water += hits;

        store.set(waterKey, water, Long.MAX_VALUE);
        store.set(lastLeakKey, now, Long.MAX_VALUE);

        return true;
    }
}
