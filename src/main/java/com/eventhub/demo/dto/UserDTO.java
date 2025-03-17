package com.eventhub.demo.dto;

public record UserDTO(String userName,
        String firstName,
        String lastName,
        String email,
        String phone,
        String address) {
}
