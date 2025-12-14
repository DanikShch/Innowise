package com.innowise.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public WebFilter loggingFilter() {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            String path = exchange.getRequest().getPath().value();
            String method = exchange.getRequest().getMethod().name();

            return chain.filter(exchange).doFinally(signal -> {
                long duration = System.currentTimeMillis() - startTime;
                int status = exchange.getResponse().getStatusCode() != null ?
                        exchange.getResponse().getStatusCode().value() : 0;

                System.out.printf("%s %s - %d ms - Status: %d%n",
                        method, path, duration, status);
            });
        };
    }
}