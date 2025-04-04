package com.eventhub.demo.event_kafka;

import com.eventhub.demo.model.Event;

import java.time.LocalDateTime;

public final class KafkaEventFactory {

    private KafkaEventFactory() {
        throw new IllegalStateException("Utility classes should not be instantiated!!");
    }

    public static EventCreated createEventCreated(Event event) {
        return new EventCreated(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getOrganizerId(),
                event.getCreatedAt()
        );
    }

    public static EventUpdated createEventUpdated(Event event) {
        return new EventUpdated(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getStartTime(),
                event.getEndTime(),
                event.getOrganizerId(),
                event.getCreatedAt()
        );
    }

    public static EventDeleted createEventDeleted(Long id) {
        return new EventDeleted(
                id,
                LocalDateTime.now()
        );
    }

}
