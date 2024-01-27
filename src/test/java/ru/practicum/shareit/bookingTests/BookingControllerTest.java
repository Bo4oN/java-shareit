package ru.practicum.shareit.bookingTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @MockBean
    BookingServiceImpl service;

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    private User user  = new User(1, "name", "test@ya.ru");
    private Item item = new Item("itemName", "itemDescription", true, user);
    private Booking booking;

    @BeforeEach
    public void setUp() throws Exception {
        item.setId(1);
        int id = 1;
        LocalDateTime startDate = LocalDateTime.now();
        LocalDateTime endDate = LocalDateTime.now().plusMonths(1);
        Status status = Status.WAITING;
        booking = new Booking(id, startDate, endDate, item, user, status);
    }


    @Test
    void addBooking() throws Exception {
        when(service.addBooking(BookingMapper.toBookingDto(booking), user.getId()))
                .thenReturn(BookingMapper.toBookingResult(booking));

        String responseString = mvc.perform(post("/bookings")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(BookingMapper.toBookingDto(booking))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(BookingMapper.toBookingResult(booking)));
        verify(service, times(1)).addBooking(BookingMapper.toBookingDto(booking), user.getId());
    }

    @Test
    void changeOfApproval() throws Exception {
        BookingResult bookingAfterChanged = BookingMapper.toBookingResult(booking);
        bookingAfterChanged.setStatus(Status.APPROVED);

        when(service.changeOfApproval(booking.getId(), false, user.getId())).thenReturn(bookingAfterChanged);

        String responseString = mvc.perform(patch("/bookings/{bookingId}", this.booking.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "false"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(responseString, mapper.writeValueAsString(bookingAfterChanged));
        verify(service, times(1)).changeOfApproval(booking.getId(), false, user.getId());
    }

    @Test
    void getBookingById() throws Exception {

        mvc.perform(get("/bookings/{bookingId}", booking.getId())
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getBookingById(booking.getId(), user.getId());
    }

    @Test
    void getAllBookingsByUser() throws Exception {

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk());

        verify(service).getAllUserBooking(State.ALL, user.getId(), 0, 10);
    }

    @Test
    void getAllBookingsForAllUserItems() throws Exception {

            mvc.perform(get("/bookings/owner")
                            .header("X-Sharer-User-Id", 1))
                    .andDo(print())
                    .andExpect(status().isOk());

            verify(service).getAllBookingsForAllUserItems(State.ALL, user.getId(), 0, 10);
        }
    }