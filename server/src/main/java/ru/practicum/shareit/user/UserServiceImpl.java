package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(int userId, UserDto userDto) {
        User userUpdate = repository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (userDto.getName() != null) {
            userUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userUpdate.setEmail(userDto.getEmail());
        }
        repository.save(userUpdate);
        return UserMapper.toUserDto(userUpdate);
    }

    @Override
    public UserDto getUser(int id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto deleteUser(int id) {
        User user = repository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        repository.deleteById(id);
        return UserMapper.toUserDto(user);
    }
}
