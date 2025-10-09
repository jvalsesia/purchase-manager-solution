package com.github.jvalsesia.pcms.infrastructure.security.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "issuer.uri")
@Component
@Getter
@Setter
public class IssuerUriConfig {
    private String protocol;
    private String host;
    private Integer port;
}
