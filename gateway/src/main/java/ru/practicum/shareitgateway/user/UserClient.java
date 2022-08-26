package ru.practicum.shareitgateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object createUser(UserDto userDto) {
        return post("", 0, userDto);
    }

    public Object getAll() {
        return get("", 0);
    }

    public Object getById(Long userId) {
        return get("/" + userId, 0);
    }

    public Object updateUser(Long userId, User user) {
        return patch("/" + userId, 0, user);
    }

    public void deleteUser(Long userId) {
        delete("/" + userId, 0);
    }
}
