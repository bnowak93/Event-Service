package com.eventhub.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@Slf4j
public class RestTemplateConfig {

    /**
     * Creates a RestTemplate with connection timeouts, read timeouts and request tracing enabled.
     * <p>
     * This RestTemplate is optimized for microservice-to-microservice communication
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // Set reasonable timeouts for service-to-service communication
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(3))
                // Add logging interceptor to track service-to-service communication
                .additionalInterceptors(loggingInterceptor())
                .build();
    }

    /**
     * Creates interceptor to log service-to-service requests
     */
    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            log.trace("Making request to: {}", request.getURI());
            return execution.execute(request, body);
        };
    }
}
