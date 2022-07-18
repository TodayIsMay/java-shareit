package ru.practicum.shareit.item;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        List<ItemDto> result = new ArrayList<>();
        for (Item item : itemRepository.getItems().values()) {
            if (item.getName().toLowerCase().contains(text.toLowerCase()) & item.getAvailable()) {
                result.add(ItemMapper.toItemDto(item));
            }
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) & item.getAvailable()) {
                result.add(ItemMapper.toItemDto(item));
            }
        }
        return result;
    }

    @Override
    public ItemDto get(long userId, long itemId) throws NoSuchElementException {
        return itemRepository.get(userId, itemId);
    }

    @Override
    public List<ItemDto> getAll(long userId) throws NoSuchElementException {
        return itemRepository.getAll(userId);
    }

    @Override
    public Item addItem(ItemDto item, long userId) throws NoSuchElementException, IllegalArgumentException {
        return itemRepository.addItem(item, userId);
    }

    @Override
    public Item editItem(long itemId, long userId, Item item) throws AccessDeniedException {
        return itemRepository.editItem(itemId, userId, item);
    }
}
