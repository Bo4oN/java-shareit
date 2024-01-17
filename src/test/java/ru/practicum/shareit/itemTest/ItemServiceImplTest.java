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
        when(itemRepository.save(testItem)).thenReturn(testItem);

        ItemDto itemDto = ItemMapper.toItemDto(testItem);
        assertEquals(itemDto, service.addItem(itemDto, testUser.getId()));
        verify(itemRepository, times(1)).save(testItem);
    }

    @Test
    public void updateItem() {
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.ofNullable(testItem));

        ItemDto updateItem = new ItemDto();
        updateItem.setName("updateName");
        updateItem.setAvailable(false);
        updateItem.setDescription("updateDescription");

        ItemDto itemAfterUpdate = service.updateItem(1, updateItem, 1);
        assertEquals(itemAfterUpdate.getName(), "updateName");
        assertEquals(itemAfterUpdate.getDescription(), "updateDescription");
        assertEquals(itemAfterUpdate.getAvailable(), false);
    }
}
