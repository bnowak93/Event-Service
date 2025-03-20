package com.eventhub.demo.validation.impl;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.validation.MinimumEventDuration;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class MinimumEventDurationValidator implements ConstraintValidator<MinimumEventDuration, EventRequestDTO> {

    private long minimumMinutes;

    public void initialize(MinimumEventDuration constraintAnnotation) {
        this.minimumMinutes = constraintAnnotation.value();
    }

    @Override
    public boolean isValid(EventRequestDTO eventRequestDTO, ConstraintValidatorContext constraintValidatorContext) {
        if (eventRequestDTO.endTime() == null || eventRequestDTO.startTime() == null) {
            return true;
        }

        Duration duration = Duration.between(eventRequestDTO.startTime(), eventRequestDTO.endTime());
        return duration.toMinutes() >= minimumMinutes;
    }
}
