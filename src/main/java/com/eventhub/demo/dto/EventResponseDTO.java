package com.eventhub.demo.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventResponseDTO(
        Long id,
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String location,
        @NotNull @Future LocalDateTime startTime,
        @NotNull @Future LocalDateTime endTime) {
}
