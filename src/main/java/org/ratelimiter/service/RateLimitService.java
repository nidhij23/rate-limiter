package org.ratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import org.ratelimiter.api.dto.RateLimitRequest;
import org.ratelimiter.api.dto.RateLimitResponse;
import org.ratelimiter.infrastructure.redis.RedisCounterStore;
import org.ratelimiter.interfaces.RateLimiter;
import org.ratelimiter.model.Identifier;
import org.ratelimiter.model.Rule;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RateLimitService {
    private RuleProviderService ruleProviderService;
    private KeyBuilderService keyBuilderService;
    private RedisCounterStore redisCounterStore;
    private RateLimiterFactory factory;

    public RateLimitService(RuleProviderService ruleProviderService,
                            KeyBuilderService keyBuilderService,
                            RedisCounterStore redisCounterStore,
                            RateLimiterFactory factory) {
        this.ruleProviderService = ruleProviderService;
        this.keyBuilderService = keyBuilderService;
        this.redisCounterStore=redisCounterStore;
        this.factory = factory;
    }
    public RateLimitResponse isAllowed(RateLimitRequest request) {
        var ruleOpt = fetchRuleFromProvider(request.getRuleName());

        Rule rule = ruleProviderService.getRule(request.getRuleName())
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        log.info("Rules: " + ruleOpt);

        if (!rule.isEnabled()) {
            return allowUnlimited();
        }

        Map<String, String> descriptorMap = request.getDescriptors()
                .stream()
                .collect(Collectors.toMap(RateLimitRequest.Descriptor::getKey, RateLimitRequest.Descriptor::getValue));

        boolean allowed = true;
        long minRemaining = Long.MAX_VALUE;
        long resetAt = System.currentTimeMillis() + rule.getWindowSeconds() * 1000;
        String algorithm = rule.getAlgorithm();

        for (Identifier identifier : rule.getIdentifiers()) {

            String value = descriptorMap.get(identifier.getType());

            // if request missing identifier -> skip limit
            if (value == null) {
                continue;
            }

            String key = keyBuilderService.buildKey(rule.getId(), identifier.getType(), value);

            RateLimiter limiter = factory.create(
                    algorithm,
                    identifier.getLimit(),
                    rule.getWindowSeconds()
            );

//            long count = redisCounterStore.incrementAndGet(
//                    key,
//                    rule.getWindowSeconds(),
//                    request.getHits()
//            );
//
//            long remaining = identifier.getLimit() - count;
//
//            if (count > identifier.getLimit()) {
//                allowed = false;
//            }
//
//            minRemaining = Math.min(minRemaining, remaining);
            allowed = limiter.allowRequest(key);
             if (!allowed) {
                 return allowUnlimited();
             }

            log.debug("Key={} minRemaining={} limit={}", key, minRemaining, identifier.getLimit());
        }

        return RateLimitResponse.builder()
                .allowed(allowed)
                .remaining(Math.max(0, minRemaining))
                .resetAt(resetAt)
                .build();
    }

    private Optional<Rule> fetchRuleFromProvider(String ruleName) {
        return ruleProviderService.getRule(ruleName);
    }

    private RateLimitResponse allowUnlimited() {
        return RateLimitResponse.builder()
                .allowed(true)
                .remaining(Long.MAX_VALUE)
                .build();
    }
}
