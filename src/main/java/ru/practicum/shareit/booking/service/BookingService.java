package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.State;

import java.util.List;

public interface BookingService {
    BookingResult addBooking(BookingDto bookingDto, int userId);

    BookingResult changeOfApproval(int bookingId, boolean isApproved, int userId);

    BookingResult getBookingById(int bookingId, int userId);

    List<BookingResult> getAllUserBooking(State status, int userId);

    List<BookingResult> getAllBookingsForAllUserItems(State status, int userId);
}
