package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        List<ItemDto.Comment> comments = item.getComments().stream()
                .map(comment -> new ItemDto.Comment(
                        comment.getId(),
                        comment.getText(),
                        comment.getAuthor().getName(),
                        comment.getCreated()
                ))
                .collect(Collectors.toList());


        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                new ItemDto.User(
                        item.getOwner().getId(),
                        item.getOwner().getName()
                ),
                comments
        );
    }

    public static List<ItemDto> toItemDtos(List<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }
}