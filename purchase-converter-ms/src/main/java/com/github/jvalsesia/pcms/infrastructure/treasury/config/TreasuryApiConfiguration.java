package com.github.jvalsesia.pcms.infrastructure.treasury.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "treasury.api")
@Component
@Getter
@Setter
public class TreasuryApiConfiguration {
    private String scheme;
    private String host;
    private String path;
}
