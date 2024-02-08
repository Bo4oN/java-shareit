package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserServiceImpl service;

    @ResponseBody
    @PostMapping
    public UserDto addUser(@RequestBody @Valid UserDto user) {
        return service.addUser(user);
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public UserDto getUser(@PathVariable int userId) {
        return service.getUser(userId);
    }

    @ResponseBody
    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable int userId, @RequestBody UserDto user) {
        return service.updateUser(userId, user);
    }

    @ResponseBody
    @DeleteMapping("/{userId}")
    public UserDto deleteUser(@PathVariable int userId) {
        return service.deleteUser(userId);
    }
}
