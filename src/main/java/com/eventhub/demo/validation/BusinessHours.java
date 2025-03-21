package com.eventhub.demo.validation;

import com.eventhub.demo.validation.impl.BusinessHoursValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = BusinessHoursValidator.class)
public @interface BusinessHours {
    String message() default "Event must take place during business hours";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int startHour() default 8;
    int endHour() default 20;
}