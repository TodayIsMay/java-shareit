package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exeptions.EntityIsAlreadyExistsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<String, User> users = new HashMap<>();
    long id = 0;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) throws NoSuchElementException {
        User targetUser = null;
        for (User user : users.values()) {
            if (user.getId() == id) {
                targetUser = user;
            }
        }
        if (targetUser == null) {
            throw new NoSuchElementException("Пользователь с таким ID не найден!");
        }
        return targetUser;
    }

    @Override
    public User addUser(User user) throws EntityIsAlreadyExistsException, IllegalArgumentException {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Email пользователя не может быть пустым!");
        }
        if (!user.getEmail().matches("^([a-z0-9_-]+\\.)*[a-z0-9_-]+@[a-z0-9_-]+(\\.[a-z0-9_-]+)*\\.[a-z]{2,6}$")) {
            throw new IllegalArgumentException("Некорректный email!");
        }
        if (users.containsKey(user.getEmail())) {
            throw new EntityIsAlreadyExistsException("Пользователь с таким email уже существует!");
        }
        if (user.getId() == 0 || users.get(user.getEmail()).getId() != user.getId()) {
            id++;
            user.setId(id);
            users.put(user.getEmail(), user);
            return user;
        } else {
            throw new EntityIsAlreadyExistsException("Такой пользователь уже существует!");
        }
    }

    @Override
    public User updateUser(long id, User user) throws NoSuchElementException, EntityIsAlreadyExistsException {
        if (users.containsKey(user.getEmail())) {
            throw new EntityIsAlreadyExistsException("Пользователь с таким email уже существует!");
        }
        User oldUser = null;
        for (User cashedUser : users.values()) {
            if (cashedUser.getId() == id) {
                oldUser = cashedUser;
            }
        }
        if (oldUser == null) {
            throw new NoSuchElementException("Пользователь не найден!");
        }
        User newUser = new User(id, user.getName() == null ? oldUser.getName() : user.getName(),
                user.getEmail() == null ? oldUser.getEmail() : user.getEmail());
        users.remove(oldUser.getEmail());
        users.put(newUser.getEmail(), newUser);
        return newUser;
    }

    @Override
    public void deleteUserById(long id) {
        for (User user : users.values()) {
            if (user.getId() == id) {
                users.remove(user.getEmail());
            }
        }
    }
}