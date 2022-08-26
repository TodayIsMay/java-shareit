package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerOrderById(User owner);

    @Query(nativeQuery = true, value = "WITH temp AS " +
            "(SELECT id, name, description, available, owner_id, request_id, ROW_NUMBER() OVER (ORDER BY id) " +
            "AS line_number " +
            "FROM items) " +
            "SELECT * FROM temp WHERE owner_id = ? AND line_number >= ? " +
            "LIMIT ?")
    List<Item> findAllByUserWithBorders(long userId, int from, int size);

    List<Item> findAllByRequestId(long requestId);
}