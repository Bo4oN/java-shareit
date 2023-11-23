package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDtoOut getItem(int itemId, int ownerId);

    List<ItemDtoWithBooking> getItemsListByOwner(int ownerId);

    List<ItemDto> searchItems(String text);

    CommentDto addComment(int itemId, Comment comment, int authorId);
}
