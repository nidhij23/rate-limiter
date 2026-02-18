package org.ratelimiter.model;

import lombok.Data;

@Data
public class Identifier {
    private String type;
    private int limit;
}
