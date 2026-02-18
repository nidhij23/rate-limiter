package org.ratelimiter.interfaces;

public interface RateLimiter {

    boolean allowRequest(String key, int hits);
}
