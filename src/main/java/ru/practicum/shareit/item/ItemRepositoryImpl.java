package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.nio.file.AccessDeniedException;
import java.util.*;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    UserRepository userRepository;

    public ItemRepositoryImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    long id = 0;
    private final Map<Long, Item> items = new HashMap<>();

    @Override
    public Map<Long, Item> getItems() {
        return items;
    }

    @Override
    public ItemDto get(long userId, long itemId) throws NoSuchElementException {
        ItemDto result = null;
        Item item = getItemById(itemId);
        if (item.getOwner().getId() == userId) {
            result = ItemMapper.toItemDto(item);
        }
        return result;
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        List<ItemDto> result = new ArrayList<>();
        boolean wasFound = false;
        for (Item item : items.values()) {
            if (item.getOwner().getId() == userId) {
                wasFound = true;
                result.add(ItemMapper.toItemDto(item));
                break;
            }
        }
        if (!wasFound) {
            throw new NoSuchElementException("Пользователь с таким ID не найден!");
        }
        return result;
    }

    @Override
    public Item getItemById(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item addItem(ItemDto item, long userId) throws NoSuchElementException, IllegalArgumentException {
        if (!isValid(item)) {
            throw new IllegalArgumentException("Позиция не может содержать пустые поля!");
        }
        User owner = userRepository.getUserById(userId);
        id++;
        Item result = new Item(id, item.getName(), item.getDescription(), item.getAvailable(), owner, null);
        items.put(id, result);
        return result;
    }

    @Override
    public Item editItem(long itemId, long userId, Item item) throws AccessDeniedException {
        if (getItemById(itemId).getOwner().getId() != userId) {
            throw new AccessDeniedException("Редактировать предмет может только его владелец!");
        }
        Item oldItem = items.get(itemId);
        String name;
        if (item.getName() == null) {
            name = oldItem.getName();
        } else {
            name = item.getName();
        }
        String description;
        if (item.getDescription() == null) {
            description = oldItem.getDescription();
        } else {
            description = item.getDescription();
        }
        Boolean isAvailable = item.getAvailable() == null ? oldItem.getAvailable() : item.getAvailable();
        Item newItem = new Item(oldItem.getId(), name, description, isAvailable, oldItem.getOwner(),
                oldItem.getRequest());
        items.replace(itemId, newItem);

        return newItem;
    }

    private boolean isValid(ItemDto item) {
        if (item.getAvailable() == null) {
            return false;
        }
        if (item.getDescription() == null || item.getDescription().isBlank() || item.getDescription().isEmpty()) {
            return false;
        }
        if (item.getName() == null || item.getName().isBlank() || item.getName().isEmpty()) {
            return false;
        }
        return true;
    }
}
