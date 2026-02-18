package org.ratelimiter.infrastructure.redis;

import org.ratelimiter.interfaces.CounterStore;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RedisCounterStore implements CounterStore {

    private final StringRedisTemplate redis;

    public RedisCounterStore(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public void increment(String key) {
        Long value = redis.opsForValue().increment(key, 1);
        return value == null ? 0 : value;
    }


    @Override
    public long get(String key) {
        String value = redis.opsForValue().get(key);
        return value == null ? 0 : Long.parseLong(value);
    }

    @Override
    public void set(String key, long value) {

    }

    @Override
    public void set(String key, long value, long ttlMillis) {
        redis.opsForValue().set(key, String.valueOf(value), Duration.ofMillis(ttlMillis));
    }

    @Override
    public void addTimestamp(String key, long timestamp) {
        redis.opsForZSet().add(key, String.valueOf(timestamp), timestamp);
    }

    @Override
    public List<Long> getTimestamps(String key) {
        Set<String> values = redis.opsForZSet().range(key, 0, -1);
        if (values == null) return List.of();

        return values.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

    @Override
    public void removeOlderThan(String key, long threshold) {
        redis.opsForZSet().removeRangeByScore(key, 0, threshold);
    }
}
