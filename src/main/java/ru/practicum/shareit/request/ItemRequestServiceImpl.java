package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemForOutRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(ItemRequestDto requestDto, int userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        ItemRequest request = repository.save(new ItemRequest(requestDto.getDescription(), user, LocalDateTime.now()));
        List<ItemForOutRequest> list = findItemsForRequest(request.getId());
        System.out.println("Список итемов тут" + list);
        return RequestMapper.toDto(request, list);
    }

    @Override
    public List<ItemRequestDto> getUserResponses(int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        List<ItemRequest> requestsList = repository.findByRequestorIdOrderByCreatedDesc(userId);
        return requestsList.stream()
                .map(request -> RequestMapper.toDto(request, findItemsForRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllResponses(int from, int size, int userId) {
        PageRequest pageable = PageRequest.of(from, size);
        List<ItemRequest> requestList = repository.findAllByRequestorIdIsNotOrderByCreated(userId, pageable).toList();
        return requestList.stream()
                .map(request -> RequestMapper.toDto(request, findItemsForRequest(request.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getResponseById(int requestId, int userId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не существует"));
        ItemRequest itemRequest = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));
        List<ItemForOutRequest> itemsList = findItemsForRequest(requestId);
        return RequestMapper.toDto(itemRequest, itemsList);
    }

    private List<ItemForOutRequest> findItemsForRequest(int requestId) {
        return itemRepository.findByItemRequestId(requestId).stream()
                .map(RequestMapper::toItemForOutRequest)
                .collect(Collectors.toList());
    }
}
