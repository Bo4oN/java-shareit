package ru.practicum.shareit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundExceptions(final NotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleDoesNotBelongToOwnerExceptions(final DoesNotBelongToOwnerException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleIsNotAvailableExceptions(final IsNotAvailableException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ItemAlreadyBookedException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemAlreadyBookedException(final Exception e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowableException(final Throwable e) {
        return new ErrorResponse(e.getMessage());
    }
}
