package com.eventhub.demo.exception;

public final class ErrorMessages {
    private ErrorMessages() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    // Resource not found messages
    public static final String EVENT_NOT_FOUND = "Event with ID %d not found";
    public static final String USER_NOT_FOUND = "User with ID %d not found";

    // Validation messages
    public static final String EVENT_END_BEFORE_START = "Event end time must be after start time";
    public static final String EVENT_START_IN_PAST = "Event start time cannot be in the past";

    // Business rule massages
    public static final String EVENT_MAX_CAPACITY_REACHED = "Event has reached maximum capacity of %d attendees";
    public static final String USER_NOT_AUTHORIZED = "User is not authorized to perform this action";
}
