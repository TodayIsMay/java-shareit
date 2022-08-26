package ru.practicum.shareitgateway.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemUpdateRequest {
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}