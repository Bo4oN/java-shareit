package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;

import java.util.List;

public class RequestMapper {

    public static ItemRequestDto toDto(ItemRequest request, List<ItemForOutRequest> itemsList) {
        return new ItemRequestDto(request.getId(), request.getText(), request.getCreated(), itemsList);
    }

    public static ItemForOutRequest toItemForOutRequest(Item item) {
        return new ItemForOutRequest(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getItemRequest().getId(),
                item.isAvailable());
    }
}
