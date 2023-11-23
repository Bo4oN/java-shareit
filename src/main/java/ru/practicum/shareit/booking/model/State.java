package ru.practicum.shareit.booking.model;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;

public enum State {
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    WAITING,
    REJECTED;

    public static State getEnumByString(String str) {
        State state;
        try {
            state = State.valueOf(str);
        } catch (IllegalArgumentException e) {
            throw new ItemAlreadyBookedException("Unknown state: " + str);
        }
        return state;
    }
}