package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingOut;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @ResponseBody
    @PostMapping
    public BookingOut addBooking(@RequestBody @Valid BookingDto bookingDto,
                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.addBooking(bookingDto, userId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingOut changeOfApproval(@PathVariable int bookingId,
                                       @RequestParam boolean approved,
                                       @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.changeOfApproval(bookingId, approved, userId);
    }

    @ResponseBody
    @GetMapping("/{bookingId}")
    public BookingOut getBookingById(@PathVariable int bookingId,
                                     @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getBookingById(bookingId, userId);
    }

    @ResponseBody
    @GetMapping
    public List<BookingOut> getAllBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                 @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getAllUserBooking(state, userId);
    }

    @ResponseBody
    @GetMapping("/owner")
    public List<BookingOut> getAllBookingsForAllUserItems(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getAllBookingsForAllUserItems(state, userId);
    }
}
