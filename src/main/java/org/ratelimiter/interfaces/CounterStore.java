package org.ratelimiter.interfaces;

import java.util.List;

public interface CounterStore {

    long increment(String key);

    long get(String key);

    void set(String key, long value, long ttlMillis);

    void addTimestamp(String key, long timestamp);

    List<Long> getTimestamps(String key);

    void removeOlderThan(String key, long threshold);
}
