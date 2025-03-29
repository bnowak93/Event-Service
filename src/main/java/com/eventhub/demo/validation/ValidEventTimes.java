package com.eventhub.demo.validation;

import com.eventhub.demo.validation.impl.EventDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEventTimes {
    String message() default "Event end time must be after start time";
    Class<?>[] groups() default{};
    Class<? extends Payload>[] payload() default {};
}
