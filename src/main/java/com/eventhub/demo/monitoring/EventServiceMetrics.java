package com.eventhub.demo.monitoring;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Getter
public class EventServiceMetrics {

    private final Counter eventCreatedCounter;
    private final Counter eventUpdatedCounter;
    private final Counter eventDeletedCounter;
    private final Timer eventCreationTimer;

    public EventServiceMetrics(MeterRegistry registry) {
        this.eventCreatedCounter = Counter.builder("events.created")
                .description("Number of events created")
                .register(registry);

        this.eventUpdatedCounter = Counter.builder("events.updated")
                .description("Number of updated events")
                .register(registry);

        this.eventDeletedCounter = Counter.builder("events.deleted")
                .description("Number of deleted events")
                .register(registry);

        this.eventCreationTimer = Timer.builder("events.creation.time")
                .description("Time taken to create events")
                .register(registry);
    }

    public void recordEventCreated() {
        eventCreatedCounter.increment();
    }

    public void recordEventUpdated() {
        eventUpdatedCounter.increment();
    }

    public void recordEventDeleted() {
        eventDeletedCounter.increment();
    }

    public void recordEventCreationTime(long timeInMs) {
        eventCreationTimer.record(timeInMs, TimeUnit.MILLISECONDS);
    }
}
