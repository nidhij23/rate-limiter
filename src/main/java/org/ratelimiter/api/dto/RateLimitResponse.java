package org.ratelimiter.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RateLimitResponse {
    private boolean allowed;
    private int limit;
    private long remaining;
    private long resetAt;
    private Integer retryAfter;
}
