package com.eventhub.demo.event_kafka;

import java.time.LocalDateTime;

public record EventUpdated(
        Long eventId,
        String title,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Long organizerId,
        LocalDateTime createdAt
) {
}
