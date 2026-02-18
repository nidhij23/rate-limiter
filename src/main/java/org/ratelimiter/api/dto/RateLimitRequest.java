package org.ratelimiter.api.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RateLimitRequest {
    private List<Descriptor> descriptors;
    private int hits;
    private long timestamp;
    private String ruleName;

    @Data
    @Builder
    public static class Descriptor {
        private String key;
        private String value;
    }
}
