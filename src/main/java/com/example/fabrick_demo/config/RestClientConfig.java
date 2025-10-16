/*package com.example.fabrick_demo.config;

import feign.RequestInterceptor;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestClientConfig {

    @Value("${clients.fabrick.api-key}")
    private String apiKey;

    @Value("${clients.fabrick.auth-schema}")
    private String authSchema;

    @Value("${clients.fabrick.time-zone}")
    private String timeZone;

    @Value("${feign.retry.period}")
    private long retryPeriod;

    @Value("${feign.retry.max-period}")
    private long retryMaxPeriod;

    @Value("${feign.retry.max-attempts}")
    private int retryMaxAttempts;

    @Bean
    public RequestInterceptor fabrickHeadersInterceptor() {
        return template -> {
            template.header("Auth-Schema", authSchema);
            template.header("Api-Key", apiKey);
            template.header("X-Time-Zone", timeZone);
        };
    }

    @Bean
    public feign.Logger.Level feignLoggerLevel() {
        return feign.Logger.Level.FULL;
    }

    @Bean
    public Retryer retryer() {
        return new Retryer.Default(retryPeriod, retryMaxPeriod, retryMaxAttempts);
    }
}
*/