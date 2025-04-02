package com.eventhub.demo.event;

import java.time.LocalDateTime;

public record EventUpdatedEvent(
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
