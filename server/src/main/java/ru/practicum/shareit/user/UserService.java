package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto user);

    UserDto updateUser(int userId, UserDto user);

    UserDto getUser(int id);

    List<UserDto> getAllUsers();

    UserDto deleteUser(int id);
}
