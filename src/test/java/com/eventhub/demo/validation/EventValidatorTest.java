package com.eventhub.demo.validation;

import com.eventhub.demo.dto.EventRequestDTO;
import com.eventhub.demo.validation.group.AdvancedValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class EventValidatorTest {

    private static ValidatorFactory validatorFactory;
    private Validator validator;
    private LocalDateTime businessHours;

    @BeforeAll
    static void setUpFactory() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
    }

    @AfterAll
    static void closeFactory() {
        if (validatorFactory != null)
            validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        validator = validatorFactory.getValidator();
        LocalDateTime now = LocalDateTime.now();
        businessHours = LocalDateTime.of(
                now.getYear(),
                now.getMonth(),
                now.getDayOfMonth() + 1,
                14,
                0);
    }

    @Test
    void whenAllFieldsValid_thenNoViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours,
                businessHours.plusHours(2),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).isEmpty();
    }

    @Test
    void whileTitleEmpty_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "",
                "This is a long description that passes validation",
                "Test Location",
                businessHours,
                businessHours.plusHours(2),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(2);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("title");
    }

    @Test
    void whenDescriptionTooShort_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "Too short",
                "Test Location",
                businessHours,
                businessHours.plusHours(2),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("description");
    }

    @Test
    void whenStartTimeInPast_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours.minusDays(2),
                businessHours.plusHours(2),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("startTime");
    }

    @Test
    void whenEndTimeBeforeStartTime_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours,
                businessHours.minusHours(1),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(1);
        // Checking if propertyPath is empty as @ValidEventTimes annotation is class level
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("");
    }

    @Test
    void whenEventIsTooShort_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours,
                businessHours.plusMinutes(30),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO, AdvancedValidation.class);

        // Then
        assertThat(violations).hasSize(1);
        // Checking if propertyPath is empty as @MinimumEventDuration annotation is class level
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("");
    }

    @Test
    void whenStartTimeOutsideBusinessHours_thenViolations() {
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours.withHour(6),
                businessHours,
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("startTime");
    }

    @Test
    void whenEndTimeOutsideBusinessHours_thenViolations(){
        // Given
        EventRequestDTO eventRequestDTO = new EventRequestDTO(
                "Test Event",
                "This is a long description that passes validation",
                "Test Location",
                businessHours,
                businessHours.withHour(21),
                null
        );

        // When
        Set<ConstraintViolation<EventRequestDTO>> violations = validator.validate(eventRequestDTO);

        // Then
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getPropertyPath().toString()).isEqualTo("endTime");
    }
}
