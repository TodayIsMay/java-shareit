package shareit.item;

import shareit.item.dto.CommentDto;
import shareit.item.dto.ItemDto;
import shareit.item.model.Item;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.NoSuchElementException;

public interface ItemService {
    List<ItemDto> search(String text, Integer from, Integer size) throws IllegalArgumentException;

    ItemDto get(long itemId, long userId) throws NoSuchElementException;

    List<ItemDto> getAll(long userId, Integer from, Integer size)
            throws NoSuchElementException, IllegalArgumentException;

    ItemDto addItem(ItemDto item, long userId) throws NoSuchElementException, IllegalArgumentException;

    Item editItem(long itemId, long userId, ItemDto item) throws AccessDeniedException;

    Item getItemById(long itemId) throws NoSuchElementException;

    CommentDto addComment(Long itemId, CommentDto comment, Long userId) throws IllegalArgumentException;
}
