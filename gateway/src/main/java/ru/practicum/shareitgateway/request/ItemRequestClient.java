package ru.practicum.shareitgateway.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareitgateway.client.BaseClient;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .build()
        );
    }

    public Object createRequest(Long userId, ItemRequestDto itemRequestDto) {
        return post("", userId, itemRequestDto);
    }

    public Object getByUser(Long userId) {
        return get("", userId);
    }

    public Object getById(Long userId, Long requestId) {
        return get("/" + requestId, userId);
    }

    public Object getAll(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = null;

        if (from != null || size != null) {
            parameters = Map.of(
                    "from", from,
                    "size", size
            );
        }

        return get("/all", userId, parameters);
    }
}
