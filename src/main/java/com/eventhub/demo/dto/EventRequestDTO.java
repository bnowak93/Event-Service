package com.eventhub.demo.dto;

import com.eventhub.demo.validation.BusinessHours;
import com.eventhub.demo.validation.MinimumEventDuration;
import com.eventhub.demo.validation.ValidEventTimes;
import com.eventhub.demo.validation.group.AdvancedValidation;
import com.eventhub.demo.validation.group.BasicValidation;
import jakarta.validation.GroupSequence;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@ValidEventTimes
@MinimumEventDuration(groups = AdvancedValidation.class, value = 45)
@GroupSequence({BasicValidation.class, EventRequestDTO.class})
public record EventRequestDTO(
        @NotBlank
        @Size(min = 5, max = 100, message = "Title must be between 5 and 100 chars")
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
        @BusinessHours
        LocalDateTime endTime,

        Long organizerId
) {
}
