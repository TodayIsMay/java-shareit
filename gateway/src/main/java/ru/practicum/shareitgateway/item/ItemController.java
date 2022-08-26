package ru.practicum.shareitgateway.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public Object getItemsOfUser(@RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal,
                                 @RequestParam(value = "from",
                                         required = false) @PositiveOrZero Integer from,
                                 @RequestParam(value = "size",
                                         required = false) @Positive Integer size) {
        log.info("Getting items of user with id " + userPrincipal);

        return itemClient.getItemsOfUser(userPrincipal, from, size);
    }

    @GetMapping("/{itemId}")
    public Object getItemById(@PathVariable("itemId") @Positive Long itemId,
                              @RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal) {
        log.info("Getting items with id " + itemId);

        return itemClient.getItemById(itemId, userPrincipal);
    }

    @PostMapping
    public Object createItem(@RequestBody @Valid ItemDto itemDto,
                             @RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal) {
        log.info("Creating items " + itemDto.toString() + " by user with id " + userPrincipal);

        return itemClient.createItem(itemDto, userPrincipal);
    }

    @PatchMapping("/{itemId}")
    public Object updateItem(@RequestBody @Valid ItemUpdateRequest itemUpdateRequest,
                             @PathVariable("itemId") @Positive Long itemId,
                             @RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal) {
        log.info("Updating items with id " + itemId);

        return itemClient.updateItem(itemUpdateRequest, itemId, userPrincipal);
    }

    @GetMapping("/search")
    public Object searchItems(@RequestParam(value = "text", required = false) String text,
                              @RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal,
                              @RequestParam(value = "from",
                                      required = false) @PositiveOrZero Integer from,
                              @RequestParam(value = "size",
                                      required = false) @Positive Integer size) {
        log.info("Searching in items by text: " + text);

        return itemClient.search(text, userPrincipal, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public Object addComment(@PathVariable("itemId") Long itemId,
                             @RequestBody @Valid CommentDto request,
                             @RequestHeader("X-Sharer-User-Id") @Positive Long userPrincipal) {
        log.info("Add comment " + request.toString() + " to item with id " + itemId +
                " by user with id " + userPrincipal);

        return itemClient.addComment(itemId, request, userPrincipal);
    }
}