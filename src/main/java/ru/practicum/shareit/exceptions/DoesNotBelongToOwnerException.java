package ru.practicum.shareit.exceptions;

public class DoesNotBelongToOwnerException extends RuntimeException {
    public DoesNotBelongToOwnerException(String message) {
        super(message);
    }
}
