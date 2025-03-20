package com.eventhub.demo.validation.impl;

import com.eventhub.demo.validation.BusinessHours;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class BusinessHoursValidator implements ConstraintValidator<BusinessHours, LocalDateTime> {
    private int startHour;
    private int endHour;

    @Override
    public void initialize(BusinessHours constraintAnnotation) {
        this.startHour = constraintAnnotation.startHour();
        this.endHour = constraintAnnotation.endHour();
    }

    @Override
    public boolean isValid(LocalDateTime localDateTime, ConstraintValidatorContext constraintValidatorContext) {
        if (localDateTime == null)
            return true;

        int hour = localDateTime.getHour();
        return hour >= startHour && hour <= endHour;
    }
}
