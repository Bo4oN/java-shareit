package ru.practicum.shareit.requestTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl service;

    private ItemRequestDto requestDto;
    private User user;

    @BeforeEach
    void setUp() {
        int id = 1;
        String name = "testName";
        String mail = "test@yandex.ru";
        user = new User(id, name, mail);

        String requestsText = "text";
        requestDto = new ItemRequestDto(requestsText);
    }

    @Test
    void addRequest() {
        ItemRequest request = new ItemRequest(requestDto.getDescription(), user, LocalDateTime.now());
        request.setId(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.save(notNull())).thenReturn(request);
        when(itemRepository.findByItemRequestId(request.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto createdRequest = service.addRequest(requestDto, user.getId());

        assertEquals(createdRequest.getDescription(), requestDto.getDescription());
        assertEquals(createdRequest.getId(), 1);
    }

    @Test
    void getUserResponse() {
        ItemRequest request = new ItemRequest(requestDto.getDescription(), user, LocalDateTime.now());
        request.setId(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(request));
        when(itemRepository.findByItemRequestId(request.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto createdRequest = service.getUserResponses(user.getId()).get(0);

        assertEquals(createdRequest.getDescription(), requestDto.getDescription());
        assertEquals(createdRequest.getId(), 1);
    }

    @Test
    void getAllResponse() {
        ItemRequest request = new ItemRequest(requestDto.getDescription(), user, LocalDateTime.now());
        request.setId(1);
        when(requestRepository.findAllByRequestorIdIsNotOrderByCreated(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(request));
        when(itemRepository.findByItemRequestId(request.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto createdRequest = service.getAllResponses(0, 10, user.getId()).get(0);

        assertEquals(createdRequest.getDescription(), requestDto.getDescription());
        assertEquals(createdRequest.getId(), 1);
    }

    @Test
    void getResponseById() {
        ItemRequest request = new ItemRequest(requestDto.getDescription(), user, LocalDateTime.now());
        request.setId(1);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        when(itemRepository.findByItemRequestId(request.getId())).thenReturn(Collections.emptyList());

        ItemRequestDto createdRequest = service.getResponseById(request.getId(), user.getId());

        assertEquals(createdRequest.getDescription(), requestDto.getDescription());
        assertEquals(createdRequest.getId(), 1);
    }
}