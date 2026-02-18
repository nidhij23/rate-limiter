package org.ratelimiter.service;

import org.springframework.stereotype.Component;

@Component
public class KeyBuilderService {

    public String buildKey(String ruleName, String identifierType, String value) {
        return "rl:" + ruleName + ":" + identifierType + "=" + sanitize(value);
    }

    private String sanitize(String input) {
        return input.replace(":", "_");
    }
}

