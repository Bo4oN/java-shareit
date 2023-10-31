package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDto getItem(int itemId);

    List<ItemDto> getItemsListByOwner(int ownerId);

    List<ItemDto> searchItems(String text);
}
