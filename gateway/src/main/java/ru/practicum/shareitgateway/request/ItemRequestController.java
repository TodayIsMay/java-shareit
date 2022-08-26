package ru.practicum.shareitgateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {

    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public Object createRequest(@RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal,
                                @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Creating item request {} by user={}", itemRequestDto, userPrincipal);

        return itemRequestClient.createRequest(userPrincipal, itemRequestDto);
    }

    @GetMapping
    public Object getByUser(@RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        log.info("Getting item requests by user={}", userId);

        return itemRequestClient.getByUser(userId);
    }

    @GetMapping("/{requestId}")
    public Object getById(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @PathVariable("requestId") @Positive Long requestId) {
        log.info("Getting item request {} by user={}", requestId, userId);

        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public Object getAll(@RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal,
                         @RequestParam(value = "from", required = false) @PositiveOrZero Integer from,
                         @RequestParam(value = "size", required = false) @Positive Integer size) {
        log.info("Getting all item requests by user={}", userPrincipal);

        return itemRequestClient.getAll(userPrincipal, from, size);
    }
}
