package com.example.fabrick_demo.config;

import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Value("${clients.fabrick.api-key}")
    private String apiKey;

    @Value("${clients.fabrick.auth-schema}")
    private String authSchema;

    @Bean
    public RequestInterceptor fabrickHeadersInterceptor() {
        return template -> {
            template.header("Auth-Schema", authSchema);
            template.header("Api-Key", apiKey);
            template.header("X-Time-Zone", "Europe/Rome");
        };
    }

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }
}
