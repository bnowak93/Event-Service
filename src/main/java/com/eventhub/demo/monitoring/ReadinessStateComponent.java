package com.eventhub.demo.monitoring;

import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Provides readiness state management for the service.
 * The API Gateway uses this information to determine if requests should be routed to this service
 */
@Component
public class ReadinessStateComponent {

    private final ApplicationEventPublisher eventPublisher;

    public ReadinessStateComponent(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Marks the application as ready to receive traffic
     */
    public void markAsReady() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.ACCEPTING_TRAFFIC);
    }

    /**
     * Marks the application as not ready to receive traffic
     * This could be used during maintenance or when shutting down
     */
    public void markAsNotReady() {
        AvailabilityChangeEvent.publish(eventPublisher, this, ReadinessState.REFUSING_TRAFFIC);
    }

    /**
     * Listens for Spring Boot lifecycle events and ensures the
     * service is properly marked as ready when fully started
     *
     * @param event Spring Boot ApplicationReady event
     */
    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        markAsReady();
    }
}
