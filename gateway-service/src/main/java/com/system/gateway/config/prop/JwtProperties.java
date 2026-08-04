package com.system.gateway.config.prop;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * 网关JWT配置。
 */
@RefreshScope
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret = "abc123456abc123456abc123456abc123456";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
