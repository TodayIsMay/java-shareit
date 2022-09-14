package shareit.itemRequest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import shareit.exeptions.EntityIsAlreadyExistsException;
import shareit.requests.ItemRequestService;
import shareit.requests.dto.ItemRequestDto;
import shareit.user.UserService;
import shareit.user.dto.UserDto;
import shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=shareit",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Test
    void getAll() throws EntityIsAlreadyExistsException {
        User user = userService.addUser(new UserDto("user1", "user1@mail.ru"));
        User observer = userService.addUser(new UserDto("another", "another@mail.ru"));


        ItemRequestDto itemRequestDto = itemRequestService.postRequest(
                new ItemRequestDto(1L, "description", null, new ArrayList<>()), user.getId());


        List<ItemRequestDto> requests = itemRequestService.getOthersRequests(observer.getId(), 0, 5);

        assertThat(requests.size(), equalTo(1));
        assertThat(requests.get(0).getId(), equalTo(itemRequestDto.getId()));
        assertThat(requests.get(0).getDescription(), equalTo(itemRequestDto.getDescription()));
        assertThat(requests.get(0).getCreated(), equalTo(itemRequestDto.getCreated()));
    }
}
