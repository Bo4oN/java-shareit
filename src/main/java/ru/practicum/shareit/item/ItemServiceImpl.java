package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.DoesNotBelongToOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage storage;
    private final UserStorage userStorage;

    @Override
    public ItemDto addItem(ItemDto itemDto, int ownerId) {
        userStorage.getUser(ownerId);
        Item item = ItemMapper.toItem(itemDto, ownerId);
        return ItemMapper.toItemDto(storage.addItem(item));
    }

    @Override
    public ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId) {
        Item item = storage.getItem(itemId);
        if (item.getOwner() != ownerId) {
            throw new DoesNotBelongToOwnerException("Запрос исходит не от владельца");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null && itemDto.getAvailable() != item.isAvailable()) {
            item.setAvailable(itemDto.getAvailable());
        }
        storage.updateItem(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItem(int itemId) {
        Item item = storage.getItem(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsListByOwner(int ownerId) {
        return ItemMapper.toItemDtoList(storage.getItemsListByOwner(ownerId));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return ItemMapper.toItemDtoList(storage.searchItems(text));
    }
}
