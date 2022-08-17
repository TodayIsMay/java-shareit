package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.Response;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private LocalDateTime created;
    private List<Item> items;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        private long id;
        private String name;
        private String description;
        private boolean available;
        private long requestId;
    }
}
