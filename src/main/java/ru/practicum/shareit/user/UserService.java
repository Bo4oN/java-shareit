package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailIsNotAvailable;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage storage;

    public User addUser(User user) {
        emailValidation(user.getEmail());
        return storage.addUser(user);
    }

    public User updateUser(int userId, User user) {
        String name = user.getName();
        String email = user.getEmail();
        User user1 = storage.getUser(userId);
        if (name != null) {
            user1.setName(name);
            storage.changeName(userId, user1);
        }
        if (email != null && !user1.getEmail().equals(email)) {
            emailValidation(user.getEmail());
            user1.setEmail(email);
            storage.changeEmail(userId, user1);
        }
        return user1;
    }

    public User getUser(int id) {
        return storage.getUser(id);
    }

    public List<User> getAllUsers() {
        if (storage.getAllUsers().isEmpty()) {
            throw new NotFoundException("Ни один пользователь не найден");
        }
        return storage.getAllUsers();
    }

    public User deleteUser(int id) {
        return storage.deleteUser(id);
    }

    private void emailValidation(String email) {
        for (User u : storage.getAllUsers()) {
            if (u.getEmail().equals(email)) {
                throw new EmailIsNotAvailable("Пользователь с такой электронной почтой уже существует");
            }
        }
    }
}
