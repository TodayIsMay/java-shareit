package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.EntityIsAlreadyExistsException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getUserById(long id) throws NoSuchElementException{
        return userRepository.getUserById(id);
    }

    @Override
    public User addUser(User user) throws EntityIsAlreadyExistsException {
        return userRepository.addUser(user);
    }

    @Override
    public User updateUser (long id, User user) throws NoSuchElementException, EntityIsAlreadyExistsException {
        return userRepository.updateUser(id, user);
    }

    @Override
    public void deleteUserById(long id) {
        userRepository.deleteUserById(id);
    }
}
