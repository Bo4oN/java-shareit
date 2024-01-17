package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingResult;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResult addBooking(BookingDto bookingDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (item.getOwner().getId() == userId) throw new NotFoundException("Нельзя забронировать своей предмет");
        if (!item.isAvailable()) throw new ItemAlreadyBookedException("Предмет не доступен");
        bookingValidation(bookingDto);
        Booking booking = BookingMapper.toBooking(bookingDto, item, user);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingResult(repository.save(booking));
    }

    @Override
    public BookingResult changeOfApproval(int bookingId, boolean isApproved, int userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Обьект бронирования не найден"));
        if (booking.getStatus() != Status.WAITING) throw new ItemAlreadyBookedException("Статус уже был изменен");
        if (item.getOwner().getId() != userId) throw new NotFoundException("Запрос не от владельца");
        if (isApproved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        repository.save(booking);
        return BookingMapper.toBookingResult(booking);
    }

    @Override
    public BookingResult getBookingById(int bookingId, int userId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование не найдено"));
        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Обьект бронирования не найден"));
        if (item.getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotFoundException("Запрос не от владельца");
        }
        return BookingMapper.toBookingResult(booking);
    }

    @Override
    public List<BookingResult> getAllUserBooking(State state, int userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Booking> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                list.addAll(repository.findByBookerIdOrderByStartDesc(userId, pageable));
                break;
            case CURRENT:
                list.addAll(repository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageable));
                break;
            case PAST:
                list.addAll(repository.findByBookerIdAndEndIsBeforeOrderByStartDesc(userId,
                        LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                list.addAll(repository.findByBookerIdAndStartIsAfterOrderByStartDesc(userId,
                        LocalDateTime.now(), pageable));
                break;
            case WAITING:
                list.addAll(repository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.WAITING, pageable));
                break;
            case REJECTED:
                list.addAll(repository.findByBookerIdAndStatusOrderByStartDesc(userId, Status.REJECTED, pageable));
                break;

            default:
                throw new ItemAlreadyBookedException("Unknown state: " + state);
        }
        return list.stream()
                .map(BookingMapper::toBookingResult)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingResult> getAllBookingsForAllUserItems(State state, int userId, int from, int size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<Item> itemslist = itemRepository.findByOwnerId(userId);
        if (itemslist.isEmpty()) throw new NotFoundException("У пользователя нет ни одного обьекта");
        List<Booking> list = new ArrayList<>();
        Pageable pageable = PageRequest.of(from / size, size);
        switch (state) {
            case ALL:
                list.addAll(repository.findAllBookingForAllUserItems(userId, pageable));
                break;
            case CURRENT:
                list.addAll(repository.findCurrentBookingsForAllUserItems(userId, LocalDateTime.now(), pageable));
                break;
            case PAST:
                list.addAll(repository.findPastBookingForAllUserItems(userId, LocalDateTime.now(), pageable));
                break;
            case FUTURE:
                list.addAll(repository.findFutureBookingForAllUserItems(userId, LocalDateTime.now(), pageable));
                break;
            case WAITING:
                list.addAll(repository.findStatusBookingForAllUserItems(userId, Status.WAITING, pageable));
                break;
            case REJECTED:
                list.addAll(repository.findStatusBookingForAllUserItems(userId, Status.REJECTED, pageable));
                break;

            default:
                throw new ItemAlreadyBookedException("Unknown state: " + state);
        }
        return list.stream()
                .map(BookingMapper::toBookingResult)
                .collect(Collectors.toList());
    }

    private void bookingValidation(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getEnd().isEqual(bookingDto.getStart()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now()) ||
                bookingDto.getEnd().isBefore(LocalDateTime.now())
        ) {
            throw new ItemAlreadyBookedException("Некорректная дата");
        }
    }
}
