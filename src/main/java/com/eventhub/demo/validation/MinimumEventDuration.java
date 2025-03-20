package com.eventhub.demo.validation;

import com.eventhub.demo.validation.impl.MinimumEventDurationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = MinimumEventDurationValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MinimumEventDuration {
    String message() default "Event duration is not long enough";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    long value() default 30;
}
