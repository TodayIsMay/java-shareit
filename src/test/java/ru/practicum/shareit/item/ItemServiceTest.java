package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeptions.EntityIsAlreadyExistsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Test
    void getUserItems() throws EntityIsAlreadyExistsException {
        User user = userService.addUser(new UserDto("user", "user@mail.com"));

        ItemDto itemDto = itemService.addItem(new ItemDto(2L,
                        "name",
                        "description",
                        true,
                        new ItemDto.User(1L, "user"),
                        null
                ),
                user.getId());

        List<ItemDto> items = itemService.getAll(user.getId(),0, 5);

        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(itemDto.getId()));
        assertThat(items.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(items.get(0).getDescription(), equalTo(itemDto.getDescription()));
        assertThat(items.get(0).getOwner().getId(), equalTo(itemDto.getOwner().getId()));
    }
}
