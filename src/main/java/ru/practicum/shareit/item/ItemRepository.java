package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public interface ItemRepository {
    Map<Long, Item> getItems();
    ItemDto get(long userId, long itemId) throws NoSuchElementException;

    List<ItemDto> getAll(long userId);

    Item getItemById(long itemId);

    Item addItem(ItemDto item, long userId) throws NoSuchElementException, IllegalArgumentException;

    Item editItem(long itemId, long userId, Item item) throws AccessDeniedException;
}