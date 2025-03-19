package com.eventhub.demo.exception;

public class InvalidEventDateException extends RuntimeException{
    public InvalidEventDateException(String message) {
        super(message);
    }
}
