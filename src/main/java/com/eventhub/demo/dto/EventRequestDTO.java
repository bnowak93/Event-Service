package com.eventhub.demo.dto;

import com.eventhub.demo.validation.BusinessHours;
import com.eventhub.demo.validation.MinimumEventDuration;
import com.eventhub.demo.validation.ValidEventDates;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@ValidEventDates
@MinimumEventDuration(value = 45)
public record EventRequestDTO(
        @NotBlank
        @Size(min = 1, max = 100, message = "Title must be between 5 and 100 chars")
        String title,

        @NotBlank
        @Size(min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
        String description,

        @NotBlank
        String location,

        @NotNull
        @Future(message = "Event start date must be in the future")
        @BusinessHours
        LocalDateTime startTime,

        @NotNull
        @Future(message = "Event end date must be in the future")
        @BusinessHours LocalDateTime endTime,

        Long organizerId
) {
}
