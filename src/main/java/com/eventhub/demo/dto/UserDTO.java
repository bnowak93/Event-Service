package com.eventhub.demo.dto;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        Long id,
        @NotBlank String userName,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String email,
        @NotBlank String phone,
        @NotBlank String address) {
}
