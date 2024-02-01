package ru.practicum.shareit.userTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    @Captor
    private ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

    private User testUser;

    @BeforeEach
    public void newTestUser() {
        int id = 1;
        String name = "testName";
        String mail = "test@yandex.ru";
        testUser = new User(id, name, mail);
    }

    @Test
    public void addUser() {
        when(repository.save(testUser)).thenReturn(testUser);

        UserDto userDto = UserMapper.toUserDto(testUser);
        assertEquals(userDto, service.addUser(userDto));
        verify(repository, times(1)).save(testUser);
    }

    @Test
    public void updateUserName() {
        when(repository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User updateUser = new User(testUser.getId(), "updateName", testUser.getEmail());
        UserDto userDto = UserMapper.toUserDto(updateUser);
        service.updateUser(testUser.getId(), userDto);

        verify(repository, times(1)).save(captor.capture());
        User capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), 1);
        assertEquals(capturedArgument.getName(), "updateName");
        assertEquals(capturedArgument.getEmail(), "test@yandex.ru");
    }

    @Test
    public void updateUserMail() {
        when(repository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        User updateUser = new User(testUser.getId(), testUser.getName(), "update@yandex.ru");
        UserDto userDto = UserMapper.toUserDto(updateUser);
        service.updateUser(testUser.getId(), userDto);

        verify(repository, times(1)).save(captor.capture());
        User capturedArgument = captor.getValue();

        assertEquals(capturedArgument.getId(), 1);
        assertEquals(capturedArgument.getName(), "testName");
        assertEquals(capturedArgument.getEmail(), "update@yandex.ru");
    }

    @Test
    public void failedUpdate() {
        when(repository.findById(anyInt())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> service.updateUser(testUser.getId(),
                UserMapper.toUserDto(testUser)));
        assertEquals(exception.getMessage(), "Пользователь не найден");
    }

    @Test
    public void getUser() {
        when(repository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        UserDto userDto = service.getUser(testUser.getId());

        assertEquals(UserMapper.toUserDto(testUser), userDto);
    }

    @Test
    public void getAllUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> list = service.getAllUsers();

        assertEquals(list.get(0), UserMapper.toUserDto(testUser));
        assertEquals(list.size(), 1);
    }

    @Test
    public void getAllWhenZeroUsers() {
        when(repository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> list = service.getAllUsers();

        assertEquals(list.size(), 0);
    }
}