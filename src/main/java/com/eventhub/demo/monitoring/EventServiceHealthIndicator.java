package com.eventhub.demo.monitoring;

import com.eventhub.demo.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventServiceHealthIndicator implements HealthIndicator {

    private final EventRepository eventRepository;

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        try {
            long eventCount = eventRepository.count();
            return Health.up()
                    .withDetail("totalEvents", eventCount)
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("error", ex.getMessage())
                    .build();
        }
    }
}
