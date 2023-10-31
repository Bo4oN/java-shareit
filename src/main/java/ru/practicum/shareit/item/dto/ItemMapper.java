package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public static Item toItem(ItemDto itemDto, int ownerId) {
        return new Item(itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                ownerId);
    }

    public static List<ItemDto> toItemDtoList(List<Item> list) {
        List<ItemDto> listDto = new ArrayList<>();
        for (Item item : list) {
            listDto.add(toItemDto(item));
        }
        return listDto;
    }
}
