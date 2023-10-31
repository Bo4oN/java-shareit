package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @ResponseBody
    @PostMapping
    public User addUser(@RequestBody @Valid User user) {
        return service.addUser(user);
    }

    @ResponseBody
    @GetMapping("/{userId}")
    public User getUser(@PathVariable int userId) {
        return service.getUser(userId);
    }

    @ResponseBody
    @GetMapping
    public List<User> getAllUsers() {
        return service.getAllUsers();
    }

    @ResponseBody
    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable int userId, @RequestBody User user) {
        return service.updateUser(userId, user);
    }

    @ResponseBody
    @DeleteMapping("/{userId}")
    public User deleteUser(@PathVariable int userId) {
        return service.deleteUser(userId);
    }
}
