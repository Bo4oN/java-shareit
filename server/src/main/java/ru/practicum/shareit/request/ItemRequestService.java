package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(ItemRequestDto text, int userId);

    List<ItemRequestDto> getUserResponses(int userId);

    List<ItemRequestDto> getAllResponses(int from, int size, int userId);

    ItemRequestDto getResponseById(int responseId, int userId);
}
