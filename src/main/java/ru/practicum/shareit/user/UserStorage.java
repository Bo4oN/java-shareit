package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class UserStorage {
    private final Map<Integer, User> users;
    private int nextId = 1;

    public User addUser(User user) {
        user.setId(nextId);
        users.put(nextId, user);
        return users.get(nextId++);
    }

    public void changeEmail(int id, User user) {
        if (users.get(id).getEmail().equals(user.getEmail())) {
            return;
        }
        users.replace(id, user);
    }

    public void changeName(int id, User user) {
        users.replace(id, user);
    }

    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User deleteUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        User user = users.get(id);
        users.remove(id);
        return user;
    }
}
