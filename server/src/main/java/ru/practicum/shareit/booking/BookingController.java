package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService service;

    @ResponseBody
    @PostMapping
    public BookingResult addBooking(@RequestBody BookingDto bookingDto,
                                    @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.addBooking(bookingDto, userId);
    }

    @ResponseBody
    @PatchMapping("/{bookingId}")
    public BookingResult changeOfApproval(@PathVariable int bookingId,
                                          @RequestParam boolean approved,
                                          @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.changeOfApproval(bookingId, approved, userId);
    }

    @ResponseBody
    @GetMapping("/{bookingId}")
    public BookingResult getBookingById(@PathVariable int bookingId,
                                        @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getBookingById(bookingId, userId);
    }

    @ResponseBody
    @GetMapping
    public List<BookingResult> getAllBookingsByUser(@RequestParam(defaultValue = "ALL") String state,
                                                    @RequestParam(defaultValue = "0") int from,
                                                    @RequestParam(defaultValue = "10") int size,
                                                    @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getAllUserBooking(State.getEnumByString(state), userId, from, size);
    }

    @ResponseBody
    @GetMapping("/owner")
    public List<BookingResult> getAllBookingsForAllUserItems(@RequestParam(defaultValue = "ALL") String state,
                                                             @RequestParam(defaultValue = "0") int from,
                                                             @RequestParam(defaultValue = "10") int size,
                                                             @RequestHeader("X-Sharer-User-Id") int userId) {
        return service.getAllBookingsForAllUserItems(State.getEnumByString(state), userId, from, size);
    }
}
