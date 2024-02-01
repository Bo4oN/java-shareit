package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoResult;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto addItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(int itemId, ItemDto itemDto, int ownerId);

    ItemDtoResult getItem(int itemId, int ownerId);

    List<ItemDtoWithBooking> getItemsListByOwner(int ownerId, int from, int size);

    List<ItemDto> searchItems(String text,int userId, int from, int size);

    CommentDto addComment(int itemId, Comment comment, int authorId);
}
