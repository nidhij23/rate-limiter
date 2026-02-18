package org.ratelimiter.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlgorithmResult {
    private boolean allowed;
    private long remaining;
    private long resetAt;
}
