package com.eventhub.demo.event_kafka;

import java.time.LocalDateTime;

public record EventDeleted(
        Long eventId,
        LocalDateTime deletedAt
) {
}
