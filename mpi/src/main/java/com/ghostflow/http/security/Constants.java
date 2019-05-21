package com.ghostflow.http.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Constants {
    @Getter
    @Setter
    @ConfigurationProperties(prefix = "constants.security")
    public static class Security {
        private String secret;
        private long expirationTime;
        private String tokenPrefix;
        private String header;
    }
}
