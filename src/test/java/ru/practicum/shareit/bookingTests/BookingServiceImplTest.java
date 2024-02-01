package ru.practicum.shareit.bookingTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BookingServiceImpl service;

    private Item testItem;
    private User testUser;
    private User secondUser;
    private BookingDto booking;

    @BeforeEach
    void newTestEntity() {
        int id = 1;
        String name = "testName";
        String mail = "test@yandex.ru";
        testUser = new User(id, name, mail);

        int secondId = 2;
        String secondName = "secondUser";
        String secondMail = "test@ya.ru";
        secondUser = new User(secondId, secondName, secondMail);

        int idItem = 1;
        String nameItem = "itemName";
        String description = "itemDescription";
        testItem = new Item(nameItem, description, true, testUser);
        testItem.setId(idItem);

        LocalDateTime start = LocalDateTime.of(2025, 1, 10, 0, 0);
        LocalDateTime end = LocalDateTime.of(2025, 2, 10, 0, 0);
        booking = new BookingDto(1, start, end, testItem.getId(), secondUser.getId(), Status.WAITING);
    }

    @Test
    void addBooking() {
        when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        BookingResult result = service.addBooking(booking, secondUser.getId());

        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getStart(), booking.getStart());
        assertEquals(result.getEnd(), booking.getEnd());
        assertEquals(result.getItem(), testItem);
        assertEquals(result.getBooker(), secondUser);
    }

    @Test
    void addBookingWithException() {
        testItem.setAvailable(false);
        when(userRepository.findById(secondUser.getId()))
                .thenReturn(Optional.of(secondUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        assertThrows(ItemAlreadyBookedException.class, () -> service.addBooking(booking, secondUser.getId()));
    }

    @Test
    void changeOfApproval() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(BookingMapper.toBooking(booking, testItem, testUser)));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        BookingResult resultApproved = service.changeOfApproval(booking.getId(), true, testUser.getId());

        assertEquals(resultApproved.getStatus(), Status.APPROVED);
    }

    @Test
    void changeOfApprovalWithException() {
        assertThrows(NotFoundException.class,
                () -> service.changeOfApproval(booking.getId(), true, testUser.getId()));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(BookingMapper.toBooking(booking, testItem, testUser)));

        assertThrows(NotFoundException.class,
                () -> service.changeOfApproval(booking.getId(), true, testUser.getId()));
    }

    @Test
    void changeOfNotApproval() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(BookingMapper.toBooking(booking, testItem, testUser)));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        BookingResult resultRejected = service.changeOfApproval(booking.getId(), false, testUser.getId());

        assertEquals(resultRejected.getStatus(), Status.REJECTED);
    }

    @Test
    void getBookingById() {
        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(BookingMapper.toBooking(booking, testItem, testUser)));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        BookingResult result = service.getBookingById(booking.getId(), testUser.getId());

        assertEquals(result.getId(), booking.getId());
        assertEquals(result.getStart(), booking.getStart());
        assertEquals(result.getEnd(), booking.getEnd());
        assertEquals(result.getItem(), testItem);
        assertEquals(result.getBooker(), testUser);
    }

    @Test
    void getBookingByIdWithException() {
        assertThrows(NotFoundException.class,
                () -> service.getBookingById(booking.getId(), testUser.getId()));

        when(bookingRepository.findById(booking.getId()))
                .thenReturn(Optional.of(BookingMapper.toBooking(booking, testItem, testUser)));

        assertThrows(NotFoundException.class,
                () -> service.getBookingById(booking.getId(), testUser.getId()));

        assertThrows(NotFoundException.class,
                () -> service.getBookingById(booking.getId(), secondUser.getId()));
    }

    @Test
    void getAllUserBookingStateALl() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdOrderByStartDesc(booking.getId(), PageRequest.of(0 / 10, 10)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.ALL, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdOrderByStartDesc(booking.getId(), PageRequest.of(0 / 10, 10));
    }

    @Test
    void getAllUserBookingStateCurrent() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.CURRENT, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
                        anyInt(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllUserBookingStatePast() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.PAST, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(
                        anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllUserBookingStateFuture() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(
                anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.FUTURE, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(
                        anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllUserBookingStateWaiting() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.WAITING, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllUserBookingStateRejected() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(bookingRepository.findByBookerIdAndStatusOrderByStartDesc(
                anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllUserBooking(State.REJECTED, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStatusOrderByStartDesc(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsAll() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findAllBookingForAllUserItems(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.ALL, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findAllBookingForAllUserItems(anyInt(), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsCurrent() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findCurrentBookingsForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.CURRENT, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findCurrentBookingsForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsPast() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findPastBookingForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.PAST, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findPastBookingForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsFuture() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findFutureBookingForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.FUTURE, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findFutureBookingForAllUserItems(anyInt(), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsWaiting() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findStatusBookingForAllUserItems(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.WAITING, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findStatusBookingForAllUserItems(anyInt(), any(Status.class), any(Pageable.class));
    }

    @Test
    void getAllBookingsForAllUserItemsRejected() {
        when(userRepository.findById(testUser.getId()))
                .thenReturn(Optional.of(testUser));
        when(itemRepository.findByOwnerId(testUser.getId())).thenReturn(List.of(testItem));
        when(bookingRepository.findStatusBookingForAllUserItems(anyInt(), any(Status.class), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.toBooking(booking, testItem, testUser)));

        service.getAllBookingsForAllUserItems(State.REJECTED, testUser.getId(), 0, 10);
        verify(bookingRepository, times(1))
                .findStatusBookingForAllUserItems(anyInt(), any(Status.class), any(Pageable.class));
    }
}