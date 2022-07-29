package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long id) throws NoSuchElementException {
        if (userRepository.findById(id).isEmpty()) {
            throw new NoSuchElementException("Пользователь с таким id не найден!");
        } else {
            return userRepository.findById(id).get();
        }
    }

    @Override
    public User addUser(UserDto userDto) throws IllegalArgumentException {
        User user = UserMapper.toUser(userDto);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(long id, User user) throws NoSuchElementException, IllegalArgumentException {
        Optional<User> oldUserOpt = userRepository.findById(id);
        if (oldUserOpt.isEmpty()) {
            throw new NoSuchElementException("Пользователь не найден!");
        }
        User oldUser = oldUserOpt.get();
        User newUser = new User(id, user.getName() == null ? oldUser.getName() : user.getName(),
                user.getEmail() == null ? oldUser.getEmail() : user.getEmail());
        return userRepository.save(newUser);
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteById(id);
    }
}