package org.ratelimiter.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "rules")
@Data
public class Rule {

    @Id
    private String id;

    private boolean enabled;
    private String algorithm;
    private long windowSeconds;

    private List<Identifier> identifiers;
}

