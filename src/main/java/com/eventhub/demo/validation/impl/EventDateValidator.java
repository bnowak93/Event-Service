package com.eventhub.demo.validation.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.validation.ValidEventTimes;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EventDateValidator implements ConstraintValidator<ValidEventTimes, EventRequestDTO> {
    @Override
    public boolean isValid(EventRequestDTO eventRequestDTO, ConstraintValidatorContext constraintValidatorContext) {
        // Skip validation if either date is null (let @NotNull handle that)
        if (eventRequestDTO.startTime() == null || eventRequestDTO.endTime() == null) {
            return true;
        }
        return eventRequestDTO.endTime().isAfter(eventRequestDTO.startTime());
    }
}
