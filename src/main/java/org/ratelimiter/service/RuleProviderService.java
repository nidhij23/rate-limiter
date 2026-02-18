package org.ratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import org.ratelimiter.infrastructure.cache.LocalCache;
import org.ratelimiter.model.Rule;
import org.ratelimiter.infrastructure.repository.RuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class RuleProviderService {

    private final LocalCache localCache;
    private final RuleRepository ruleRepository;

    @Autowired
    public RuleProviderService(LocalCache localCache, RuleRepository ruleRepository) {
        this.localCache = localCache;
        this.ruleRepository = ruleRepository;
    }

    public Optional<Rule> getRule(String key) {
        try {
            // 1️⃣ Try in-memory cache (fastest)
            Optional<Rule> rule = localCache.getRule(key);
            if (rule.isPresent()) {
                return rule;
            }

            // 2️⃣ Fetch from MongoDB (source of truth)
            rule = ruleRepository.findById(key);

            // 3️⃣ Populate cache for next requests
            rule.ifPresent(r -> localCache.putRule(key, r));

            return rule;
        } catch (Exception e) {
            log.info("Rule not found for key: " + key +
                    ", error: " + e.getMessage());
            return Optional.empty();
        }
    }
}
