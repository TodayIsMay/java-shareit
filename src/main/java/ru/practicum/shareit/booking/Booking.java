package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDate;

/**
 * // TODO реализовать сущность для бронирования вещей.
 */
@Data
public class Booking {
    private long id;
    private LocalDate start;
    private LocalDate end;
    private Item item;
    private User booker;
    private Status status;
}
