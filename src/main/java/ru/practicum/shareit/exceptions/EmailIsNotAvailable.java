package ru.practicum.shareit.exceptions;

public class EmailIsNotAvailable extends RuntimeException {
    public EmailIsNotAvailable(String message) {
        super(message);
    }
}
