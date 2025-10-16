package com.example.fabrick_demo.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${clients.fabrick.base-url}")
    private String baseUrl;

    @Value("${clients.fabrick.auth-schema}")
    private String authSchema;

    @Value("${clients.fabrick.api-key}")
    private String apiKey;

    @Bean
    public WebClient fabrickWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Auth-Schema", authSchema)
                .defaultHeader("Api-Key", apiKey)
                .build();
    }
}