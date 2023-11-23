package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.model.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
public class BookingDto {
    private int id;
    @NotNull
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private int itemId;
    private int bookerId;
    private Status status;
}
