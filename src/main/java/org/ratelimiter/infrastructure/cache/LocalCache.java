package org.ratelimiter.infrastructure.cache;

import org.ratelimiter.model.Rule;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LocalCache {

    private final Map<String, Rule> cache = new ConcurrentHashMap<>();

    public Optional<Rule> getRule(String key) {
        return Optional.ofNullable(cache.get(key));
    }

    public void putRule(String key, Rule rule) {
        cache.put(key, rule);
    }

    public void evictRule(String key) {
        cache.remove(key);
    }

    public void clear() {
        cache.clear();
    }
}
