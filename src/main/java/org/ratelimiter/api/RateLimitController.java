package org.ratelimiter.api;

import org.ratelimiter.api.dto.RateLimitRequest;
import org.ratelimiter.api.dto.RateLimitResponse;
import org.ratelimiter.service.RateLimitService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ratelimit")
public class RateLimitController {

    private final RateLimitService rateLimitService;

    public RateLimitController(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;
    }

    @PostMapping("/check")
    public RateLimitResponse check(@RequestBody RateLimitRequest request) {
        return rateLimitService.isAllowed(request);
    }
}
