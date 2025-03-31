package com.eventhub.demo.monitoring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain
    ) throws ServletException, IOException {
        // Generate unique request ID
        String requestId = UUID.randomUUID().toString();

        // Add request ID to MDC for logging
        org.slf4j.MDC.put("requestId", requestId);

        // Add request ID to response header
        response.addHeader("X-Request-ID", requestId);

        // Log request
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String path = queryString == null ? uri : uri + "?" + queryString;

        // Measure process time
        Instant startTime = Instant.now();

        try {
            // Pass to the next filter
            filterChain.doFilter(request, response);
        } finally{
            // Calculate and log processing time
            long processingTimeMs = Duration.between(startTime, Instant.now()).toMillis();
            int status = response.getStatus();

            log.info("Completed request: {} {} - {} in {} ms", method, path, status, processingTimeMs);

            // Clean up MDC
            org.slf4j.MDC.remove("requestId");
        }
    }
}
