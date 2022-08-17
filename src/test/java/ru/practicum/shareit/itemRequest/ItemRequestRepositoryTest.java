package ru.practicum.shareit.itemRequest;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.requests.ItemRequestRepository;
import ru.practicum.shareit.requests.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private ItemRequest itemRequest;

    private User user;

    private Item item;

    @BeforeEach
    void init() {
        itemRequest = new ItemRequest();

        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("desc");

        user = new User();

        user.setName("name");
        user.setEmail("mail@mail.com");

        item = new Item();

        item.setName("name");
        item.setRequest(null);
        item.setAvailable(true);
        item.setDescription("desc");
    }

    @Test
    @Order(1)
    @Rollback(value = false)
    void saveRequest() {
        userRepository.save(user);

        item.setOwner(user);

        itemRepository.save(item);

        itemRequest.setRequester(user);
        itemRequest.setItems(List.of(item));

        repository.save(itemRequest);

        Assertions.assertEquals(1L, itemRequest.getId());
    }

    @Test
    @Order(2)
    @Rollback(value = false)
    void getByRequester() {
        List<ItemRequest> requests = repository.findAllByRequesterIdOrderByCreatedDesc(userRepository.findById(1L).get().getId());

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(1L, requests.get(0).getId());
    }

    @Test
    @Order(3)
    @Rollback(value = false)
    void findAll() {
        List<ItemRequest> requests = repository.findOthersRequests(0, 5);

        Assertions.assertEquals(1, requests.size());
        Assertions.assertEquals(1L, requests.get(0).getId());
    }
}
