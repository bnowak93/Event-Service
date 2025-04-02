package com.eventhub.demo.event;

import java.time.LocalDateTime;

public record EventDeletedEvent(
        Long eventId,
        LocalDateTime deletedAt
) {
}
