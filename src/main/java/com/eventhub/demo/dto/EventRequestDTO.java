package com.eventhub.demo.dto;

import com.eventhub.demo.validation.BusinessHours;
import com.eventhub.demo.validation.MinimumEventDuration;
import com.eventhub.demo.validation.ValidEventDates;
import com.eventhub.demo.validation.group.BasicValidation;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@ValidEventDates(groups = BasicValidation.class)
@MinimumEventDuration(groups = BasicValidation.class, value = 45)
public record EventRequestDTO(
        @NotBlank(groups = BasicValidation.class)
        @Size(groups = BasicValidation.class, min = 5, max = 100, message = "Title must be between 5 and 100 chars")
        String title,

        @NotBlank(groups = BasicValidation.class)
        @Size(groups = BasicValidation.class, min = 20, max = 2000, message = "Description must be between 20 and 2000 characters")
        String description,

        @NotBlank(groups = BasicValidation.class)
        String location,

        @NotNull(groups = BasicValidation.class)
        @Future(message = "Event start date must be in the future")
        @BusinessHours
        LocalDateTime startTime,

        @NotNull(groups = BasicValidation.class)
        @Future(groups = BasicValidation.class, message = "Event end date must be in the future")
        @BusinessHours LocalDateTime endTime,

        Long organizerId
) {
}
