package ru.practicum.shareit.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    @Column(name = "request_id")
    private long requestId;
    @Column(name = "item_id")
    private long itemId;
    @Column
    private String name;
    @Column(name = "owner_id")
    private long ownerId;
}
