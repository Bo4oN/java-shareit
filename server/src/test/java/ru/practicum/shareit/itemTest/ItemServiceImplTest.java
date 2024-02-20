package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.DoesNotBelongToOwnerException;
import ru.practicum.shareit.exceptions.ItemAlreadyBookedException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemForOutRequest;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl service;

    private Item testItem;
    private User testUser;

    @BeforeEach
    void newTestEntity() {
        int id = 1;
        String name = "testName";
        String mail = "test@yandex.ru";
        testUser = new User(id, name, mail);

        int idItem = 1;
        String nameItem = "itemName";
        String description = "itemDescription";
        testItem = new Item(nameItem, description, true, testUser);
        testItem.setId(idItem);
    }

    @Test
    void addItem() {
        ItemRequest request = new ItemRequest(
                 "text", testUser, LocalDateTime.of(2023, 10, 10, 0, 0));
        request.setId(1);
        Item item = new Item("itemName", "itemDescription", true, testUser, request);
        item.setId(2);
        ItemForOutRequest itemForOutRequest = RequestMapper.toItemForOutRequest(item);
        when(itemRepository.save(notNull())).thenReturn(item);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));


        ItemDto itemDto = ItemMapper.toItemDto(item);
        itemDto.setRequestId(1);
        ItemDto createdItem = service.addItem(itemDto, testUser.getId());
        assertEquals(itemDto, createdItem);
        verify(itemRepository, times(1)).save(notNull());
    }


    @Test
    void updateItem() {
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(itemRepository.save(notNull())).thenReturn(testItem);

        ItemDto updateItem = new ItemDto();
        updateItem.setName("updateName");
        updateItem.setAvailable(false);
        updateItem.setDescription("updateDescription");

        ItemDto itemAfterUpdate = service.updateItem(1, updateItem, 1);

        assertEquals(itemAfterUpdate.getName(), "updateName");
        assertEquals(itemAfterUpdate.getDescription(), "updateDescription");
        assertEquals(itemAfterUpdate.getAvailable(), false);
    }

    @Test
    void updateItemWithException() {
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        ItemDto updateItem = new ItemDto();

        assertThrows(DoesNotBelongToOwnerException.class, () -> service.updateItem(1, updateItem, 2));
    }

    @Test
    void getItem() {
        Booking booking1 = new Booking(1,
                LocalDateTime.of(2023, 12, 20, 0, 0),
                LocalDateTime.of(2023, 12, 30, 0, 0),
                testItem,
                testUser,
                Status.WAITING);
        Booking booking2 = new Booking(2,
                LocalDateTime.of(2025, 1, 20, 0, 0),
                LocalDateTime.of(2025, 1, 30, 0, 0),
                testItem,
                testUser,
                Status.WAITING);
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        when(commentRepository.findByItemId(testItem.getId())).thenReturn(Collections.emptyList());
        when(bookingRepository.findAllByItemIdOrderByStart(testItem.getId()))
                .thenReturn(List.of(booking1, booking2));

        ItemDtoResult itemDtoResult = ItemMapper.toItemDtoResult(testItem);
        itemDtoResult.setLastBooking(BookingMapper.toBookingDto(booking1));
        itemDtoResult.setNextBooking(BookingMapper.toBookingDto(booking2));

        ItemDtoResult itemDto = service.getItem(testItem.getId(), testUser.getId());

        assertEquals(itemDto, itemDtoResult);
    }

    @Test
    void getItemsListByOwner() {
        Item item2 = new Item("item2", "description2", true, testUser);

        when(itemRepository.findByOwnerIdOrderById(testUser.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(testItem, item2));

        List<ItemDtoWithBooking> list = service.getItemsListByOwner(testUser.getId(), 0, 10);

        assertEquals(list.get(0), ItemMapper.toItemDtoWithBookingDate(testItem));
        assertEquals(list.get(1), ItemMapper.toItemDtoWithBookingDate(item2));
        assertEquals(list.size(), 2);
    }

    @Test
    void searchItems() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(itemRepository.search("test", PageRequest.of(0, 10))).thenReturn(List.of(testItem));

        assertEquals(service.searchItems("", testUser.getId(), 0, 10).size(), 0);
        assertEquals(service.searchItems("test", testUser.getId(), 0, 10).size(), 1);
        assertEquals(service.searchItems(
                "test", testUser.getId(), 0, 10).get(0), ItemMapper.toItemDto(testItem));
    }

    @Test
    void addComment() {
        Comment comment = new Comment();
        comment.setText("text");

        when(bookingRepository.findByItemIdAndBookerIdAndEndIsBefore(
                anyInt(),
                anyInt(),
                any(LocalDateTime.class))).thenReturn(List.of(new Booking()));

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        CommentDto commentDto = service.addComment(testItem.getId(), comment, testUser.getId());

        assertEquals(commentDto.getAuthorName(), "testName");
        assertEquals(commentDto.getText(), "text");
    }

    @Test
    void addCommentWithException() {
        Comment comment = new Comment();
        comment.setText("text");

        when(bookingRepository.findByItemIdAndBookerIdAndEndIsBefore(
                anyInt(),
                anyInt(),
                any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        assertThrows(ItemAlreadyBookedException.class,
                () -> service.addComment(testItem.getId(), comment, testUser.getId()));
    }
}
