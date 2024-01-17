package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private ItemServiceImpl service;

    @Captor
    private ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);

    private Item testItem;

    private User testUser;

    @BeforeEach
    public void newTestEntity() {
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
    public void addItem() {
        when(itemRepository.save(notNull())).thenReturn(testItem);
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        ItemDto itemDto = ItemMapper.toItemDto(testItem);
        ItemDto createdItem = service.addItem(itemDto, testUser.getId());
        assertEquals(itemDto, createdItem);
        verify(itemRepository, times(1)).save(notNull());
    }
}
