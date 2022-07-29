package ru.practicum.shareit.user;

import ru.practicum.shareit.exeptions.EntityIsAlreadyExistsException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;

public interface UserService {
    List<User> getAll();

    User getUserById(long id) throws NoSuchElementException;

    User addUser(UserDto user) throws EntityIsAlreadyExistsException;

    User updateUser(long id, User user) throws NoSuchElementException, EntityIsAlreadyExistsException,
            IllegalArgumentException;

    void deleteUserById(long id);
}
