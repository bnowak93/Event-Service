package com.eventhub.demo.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String message,
        String path,
        int status,
        LocalDateTime timeStamp
) {
}
