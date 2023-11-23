package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;

import java.util.List;

public interface BookingService {
    BookingOut addBooking(BookingDto bookingDto, int userId);

    BookingOut changeOfApproval(int bookingId, boolean isApproved, int userId);

    BookingOut getBookingById(int bookingId, int userId);

    List<BookingOut> getAllUserBooking(String status, int userId);

    List<BookingOut> getAllBookingsForAllUserItems(String status, int userId);
}
