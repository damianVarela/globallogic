package com.evaluation.globallogic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "security.jwt")
public class TokenProperties {

    private String prefix = "Bearer ";

    private int expiration;

    private String secret;
}
