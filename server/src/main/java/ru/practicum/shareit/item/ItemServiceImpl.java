package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exceptions.DoesNotBelongToOwnerException;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            item.setItemRequest(request);
            ItemDto itemDtoOut = ItemMapper.toItemDto(repository.save(item));
            itemDtoOut.setRequestId(request.getId());
            return itemDtoOut;
        }
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        Item i = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (i.getOwner().getId() != ownerId) {
            throw new DoesNotBelongToOwnerException("Запрос исходит не от владельца");
        }
        if (itemDto.getAvailable() != null) i.setAvailable(itemDto.getAvailable());
        if (itemDto.getDescription() != null) i.setDescription(itemDto.getDescription());
        if (itemDto.getName() != null) i.setName(itemDto.getName());
        repository.save(i);
        return ItemMapper.toItemDto(i);
    }

    @Override
    public ItemDtoResult getItem(int itemId, int ownerId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        ItemDtoResult itemDto = ItemMapper.toItemDtoResult(item);
        List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStart(item.getId());
        if (item.getOwner().getId() == ownerId) {
            for (int i = 0; i < bookingList.size(); i++) {
                if (bookingList.get(i).getStart().isBefore(LocalDateTime.now())) {
                    itemDto.setLastBooking(BookingMapper.toBookingDto(bookingList.get(i)));
                    if ((i + 1) != bookingList.size()) {
                        itemDto.setNextBooking(BookingMapper.toBookingDto(bookingList.get(i + 1)));
                    }
                }
            }
        }
        itemDto.setComments(commentRepository.findByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
            System.out.println(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getItemsListByOwner(int ownerId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Item> itemList = repository.findByOwnerIdOrderById(ownerId, pageable);
        List<ItemDtoWithBooking> itemListWithBooking = itemList.stream()
                .map(ItemMapper::toItemDtoWithBookingDate)
                .collect(Collectors.toList());

        for (ItemDtoWithBooking item : itemListWithBooking) {
            List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStart(item.getId());
            for (int i = 0; i < bookingList.size(); i++) {
                if (bookingList.get(i).getStart().isBefore(LocalDateTime.now())) {
                    item.setLastBooking(BookingMapper.toBookingDto(bookingList.get(i)));
                    if ((i + 1) != bookingList.size()) {
                        item.setNextBooking(BookingMapper.toBookingDto(bookingList.get(i + 1)));
                    }
                }
            }
        }
        return itemListWithBooking;
    }

    @Override
    public List<ItemDto> searchItems(String text, int userId, int from, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (text.isBlank()) return Collections.emptyList();
        Pageable pageable = PageRequest.of(from, size);
        List<Item> list = repository.search(text, pageable);
        return ItemMapper.toItemDtoList(list);
    }

    @Override
    public CommentDto addComment(int itemId, Comment comment, int authorId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndIsBefore(
                itemId,
                authorId,
                LocalDateTime.now());
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (bookings.isEmpty()) {
            throw new ItemAlreadyBookedException("Пользователь не может оставлять отзывы к этому предмету");
        }
        comment.setAuthor(user);
        comment.setItem(item);
        comment.setCreatedDate(LocalDateTime.now());
        commentRepository.save(comment);
        return CommentMapper.toCommentDto(comment);
    }
}
