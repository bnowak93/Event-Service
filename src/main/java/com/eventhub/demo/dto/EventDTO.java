package com.eventhub.demo.dto;

import java.time.LocalDateTime;

public record EventDTO(String title,
                       String description,
                       String location,
                       LocalDateTime startTime,
                       LocalDateTime endTime) {
}
