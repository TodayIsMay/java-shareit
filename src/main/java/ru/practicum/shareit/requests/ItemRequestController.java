package ru.practicum.shareit.requests;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private ItemRequestService itemRequestService;

    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }

    @PostMapping
    public ItemRequestDto postRequest(@RequestBody ItemRequestDto itemRequestDto,
                                      @RequestHeader(value = "X-Sharer-User-Id") long requesterId)
            throws NoSuchElementException, IllegalArgumentException {
        return itemRequestService.postRequest(itemRequestDto, requesterId);
    }

    @GetMapping
    public List<ItemRequestDto> getOwnRequests(@RequestHeader(value = "X-Sharer-User-Id") long requesterId)
            throws NoSuchElementException {
        return itemRequestService.getRequests(requesterId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getOthersRequests(@RequestHeader(value = "X-Sharer-User-Id") long requesterId,
                                                  @RequestParam(required = false) Integer from,
                                                  @RequestParam(required = false) Integer size) {
        return itemRequestService.getOthersRequests(requesterId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable long requestId,
                                         @RequestHeader(value = "X-Sharer-User-Id") long requesterId)
            throws NoSuchElementException {
        return itemRequestService.getItemRequest(requestId, requesterId);
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleIllegalArgument(final IllegalArgumentException e) {
        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> handleNoSuchElement(final NoSuchElementException e) {
        return new ResponseEntity<>(
                Map.of("message", e.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }
}