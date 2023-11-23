package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.exceptions.DoesNotBelongToOwnerException;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
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

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item = ItemMapper.toItem(itemDto, ownerId);
        return ItemMapper.toItemDto(repository.save(item));
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        Item i = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        if (i.getOwnerId() != ownerId) {
            throw new DoesNotBelongToOwnerException("Запрос исходит не от владельца");
        }
        if (itemDto.getAvailable() != null) i.setAvailable(itemDto.getAvailable());
        if (itemDto.getDescription() != null) i.setDescription(itemDto.getDescription());
        if (itemDto.getName() != null) i.setName(itemDto.getName());
        return ItemMapper.toItemDto(repository.save(i));
    }

    @Override
    public ItemDtoOut getItem(int itemId, int ownerId) {
        Item item = repository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        ItemDtoOut itemDto = ItemMapper.toItemDtoOut(item);
        List<Booking> bookingList = bookingRepository.findAllByItemIdOrderByStart(item.getId());
        if (item.getOwnerId() == ownerId) {
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
        return itemDto;
    }

    @Override
    public List<ItemDtoWithBooking> getItemsListByOwner(int ownerId) {
        List<Item> itemList = repository.findByOwnerId(ownerId);
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
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return ItemMapper.toItemDtoList(repository.search(text));
    }

    @Override
    public CommentDto addComment(int itemId, Comment comment, int authorId) {
        List<Booking> bookings = bookingRepository.findByItemIdAndBookerIdAndEndIsBefore(
                itemId,
                authorId,
                LocalDateTime.now());
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (bookings.isEmpty()) {
            throw new ItemAlreadyBookedException("Пользователь не может оставлять отзывы к этому предмету");
        }
        comment.setAuthor(user.getName());
        comment.setItemId(itemId);
        comment.setCreatedDate(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
